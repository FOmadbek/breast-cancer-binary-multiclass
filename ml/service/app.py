"""
FastAPI inference service for the breast-cancer detection system.

Serves TWO models:
  - binary  : benign vs malignant     (safety-critical detector; threshold-tuned)
  - subtype : 8 histological subtypes  (extra detail only, NOT the safety decision)

Endpoint:
  POST /predict   (multipart image file)  ->  JSON with both results
  GET  /health    quick liveness check

The Java patient-management backend calls /predict, then attaches the patient
info (name, gender, id, passport, test date) and stores everything in SQL.
This service knows nothing about patients it only turns an image into numbers.
"""

import io
import json
from pathlib import Path

import torch
import torch.nn.functional as F
from torchvision import transforms
from torchvision.models import resnet18
from PIL import Image
from fastapi import FastAPI, UploadFile, File, HTTPException

MODEL_DIR = Path(__file__).parent.parent / "model"

BINARY_WEIGHTS  = MODEL_DIR / "resnet18_breakhis_200x.pt"
BINARY_META     = MODEL_DIR / "metadata.json"
SUBTYPE_WEIGHTS = MODEL_DIR / "resnet18_multiclass_200x.pt"
SUBTYPE_META    = MODEL_DIR / "metadata_multiclass_200x.json"

# Inference is lightCPU is perfectly fine for serving. Uses GPU if present.
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")


def load_bundle(weights_path: Path, meta_path: Path) -> dict:
    """Load one model + its metadata + the matching preprocessing transform.

    The transform MUST match the notebook's eval transform exactly (same size,
    same mean/std) or predictions silently go wrong that's the parity rule.
    We read those values straight from the saved metadata, so they can't drift.
    """
    meta = json.loads(Path(meta_path).read_text())

    model = resnet18(weights=None)  # architecture only; real weights come from the .pt
    model.fc = torch.nn.Linear(model.fc.in_features, len(meta["classes"]))
    state = torch.load(weights_path, map_location=DEVICE)
    model.load_state_dict(state)
    model.eval().to(DEVICE)

    tfm = transforms.Compose([
        transforms.Resize((meta["img_size"], meta["img_size"])),
        transforms.ToTensor(),
        transforms.Normalize(meta["mean"], meta["std"]),
    ])
    return {"model": model, "meta": meta, "tfm": tfm}


# Load BOTH models once at startup, not per request (loading is the slow part).
binary_bundle  = load_bundle(BINARY_WEIGHTS, BINARY_META)
subtype_bundle = load_bundle(SUBTYPE_WEIGHTS, SUBTYPE_META)

app = FastAPI(title="Breast Cancer Detection Service", version="1.0")


@torch.no_grad()
def infer(bundle: dict, image: Image.Image):
    """Run one image through one model, return (probabilities, class_names)."""
    x = bundle["tfm"](image).unsqueeze(0).to(DEVICE)   # add batch dim
    probs = F.softmax(bundle["model"](x), dim=1)[0].cpu()
    return probs, bundle["meta"]["classes"]


@app.get("/health")
def health():
    return {"status": "ok", "device": str(DEVICE)}


@app.post("/predict")
async def predict(file: UploadFile = File(...)):

    try:
        raw = await file.read()
        image = Image.open(io.BytesIO(raw)).convert("RGB")
    except Exception:
        raise HTTPException(status_code=400, detail="Invalid or unreadable image file.")

    b_probs, b_classes = infer(binary_bundle, image)
    mal_idx = b_classes.index("malignant")
    p_malignant = float(b_probs[mal_idx])
    tau = float(binary_bundle["meta"].get("threshold", 0.5))   # recall-tuned cutoff
    binary_label = "malignant" if p_malignant >= tau else "benign"

    s_probs, s_classes = infer(subtype_bundle, image)
    s_idx = int(torch.argmax(s_probs))

    return {
        "binary": {
            "label": binary_label,
            "probability_malignant": round(p_malignant, 4),
            "threshold": tau,
        },
        "subtype": {
            "label": s_classes[s_idx],
            "confidence": round(float(s_probs[s_idx]), 4),
        },
    }
