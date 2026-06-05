package com.albert.patientsystem.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import com.albert.patientsystem.entity.DiagnosticRecord;
import com.albert.patientsystem.repository.DiagnosticRecordRepository;
import com.albert.patientsystem.service.DiagnosisService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DiagnosisController {
    private final DiagnosticRecordRepository recordRepo;
    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService,
                               DiagnosticRecordRepository recordRepo) {
        this.diagnosisService = diagnosisService;
        this.recordRepo = recordRepo;
    }
    @PutMapping("/records/{id}/verify") // Removed the extra /api here
    public ResponseEntity<?> verifyRecord(@PathVariable Long id, @RequestBody java.util.Map<String, String> payload) {
        // Changed diagnosticRecordRepository to recordRepo
        DiagnosticRecord record = recordRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setVerified(true);
        // If the doctor sent a correction, save it. Otherwise, it stays null.
        if(payload.containsKey("correctedLabel")) {
            record.setCorrectedLabel(payload.get("correctedLabel"));
        }


        // Changed diagnosticRecordRepository to recordRepo
        recordRepo.save(record);
        return ResponseEntity.ok(record);
    }
    // upload an image for a patient -> predict -> save
    @PostMapping(value = "/patients/{patientId}/diagnose", consumes = "multipart/form-data")
    public DiagnosticRecord diagnose(
            @PathVariable Long patientId,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "testDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate testDate) {
        return diagnosisService.diagnose(patientId, image, testDate);
    }
    @GetMapping("/records/{id}")
    public DiagnosticRecord getRecord(@PathVariable Long id) {
        return recordRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    }

    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        if (!recordRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found");
        recordRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // list all diagnostic records for a patient
    @GetMapping("/patients/{patientId}/records")
    public List<DiagnosticRecord> records(@PathVariable Long patientId) {
        return recordRepo.findByPatient_Id(patientId);
    }
    @GetMapping("/records")
    public Page<DiagnosticRecord> listRecords(
            @RequestParam(required = false) String label,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("testDate").descending());
        return diagnosisService.searchRecords(label, from, to, pageable);
    }
}