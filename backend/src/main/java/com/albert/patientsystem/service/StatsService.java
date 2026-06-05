package com.albert.patientsystem.service;

import com.albert.patientsystem.repository.DiagnosticRecordRepository;
import com.albert.patientsystem.repository.PatientRepository;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StatsService {

    private final PatientRepository patientRepo;
    private final DiagnosticRecordRepository recordRepo;

    public StatsService(PatientRepository patientRepo, DiagnosticRecordRepository recordRepo) {
        this.patientRepo = patientRepo;
        this.recordRepo = recordRepo;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalPatients", patientRepo.count());
        stats.put("totalDiagnoses", recordRepo.count());
        stats.put("malignant", recordRepo.countByBinaryLabel("malignant"));
        stats.put("benign", recordRepo.countByBinaryLabel("benign"));

        Map<String, Long> subtypes = new LinkedHashMap<>();
        for (Object[] row : recordRepo.countBySubtype()) {
            String label = (String) row[0];
            subtypes.put(label != null ? label : "unknown", (Long) row[1]);
        }
        stats.put("subtypeDistribution", subtypes);
        return stats;
    }
}