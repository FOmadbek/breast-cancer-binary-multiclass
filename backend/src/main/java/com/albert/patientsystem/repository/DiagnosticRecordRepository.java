package com.albert.patientsystem.repository;

import com.albert.patientsystem.entity.DiagnosticRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiagnosticRecordRepository extends JpaRepository<DiagnosticRecord, Long> {

    // the underscore navigates the relationship: record -> patient -> id
    List<DiagnosticRecord> findByPatient_Id(Long patientId);
    List<DiagnosticRecord> findByPatient_PatientId(String patientId);
}