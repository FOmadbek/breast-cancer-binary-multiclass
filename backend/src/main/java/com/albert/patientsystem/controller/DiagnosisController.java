package com.albert.patientsystem.controller;

import com.albert.patientsystem.entity.DiagnosticRecord;
import com.albert.patientsystem.repository.DiagnosticRecordRepository;
import com.albert.patientsystem.service.DiagnosisService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;
    private final DiagnosticRecordRepository recordRepo;

    public DiagnosisController(DiagnosisService diagnosisService,
                               DiagnosticRecordRepository recordRepo) {
        this.diagnosisService = diagnosisService;
        this.recordRepo = recordRepo;
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

    // list all diagnostic records for a patient
    @GetMapping("/patients/{patientId}/records")
    public List<DiagnosticRecord> records(@PathVariable Long patientId) {
        return recordRepo.findByPatient_Id(patientId);
    }
}