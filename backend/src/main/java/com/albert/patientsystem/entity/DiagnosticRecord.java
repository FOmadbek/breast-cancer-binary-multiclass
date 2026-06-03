package com.albert.patientsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostic_records")
public class DiagnosticRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")       // foreign key -> patients.id
    private Patient patient;

    private LocalDate testDate;            // day the test was taken
    private String imageFilename;

    private String binaryLabel;            // BENIGN / MALIGNANT  (binary model)
    private Double binaryProbability;      // probability_malignant

    private String subtypeLabel;           // e.g. ductal_carcinoma (subtype model)
    private Double subtypeConfidence;

    private LocalDateTime createdAt;

    public DiagnosticRecord() {}

    @PrePersist
    public void onCreate() {               // auto-set timestamp on insert
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public LocalDate getTestDate() { return testDate; }
    public void setTestDate(LocalDate testDate) { this.testDate = testDate; }
    public String getImageFilename() { return imageFilename; }
    public void setImageFilename(String imageFilename) { this.imageFilename = imageFilename; }
    public String getBinaryLabel() { return binaryLabel; }
    public void setBinaryLabel(String binaryLabel) { this.binaryLabel = binaryLabel; }
    public Double getBinaryProbability() { return binaryProbability; }
    public void setBinaryProbability(Double binaryProbability) { this.binaryProbability = binaryProbability; }
    public String getSubtypeLabel() { return subtypeLabel; }
    public void setSubtypeLabel(String subtypeLabel) { this.subtypeLabel = subtypeLabel; }
    public Double getSubtypeConfidence() { return subtypeConfidence; }
    public void setSubtypeConfidence(Double subtypeConfidence) { this.subtypeConfidence = subtypeConfidence; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}