package com.albert.patientsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class PatientRequest {

    @NotBlank(message = "patientId is required")
    private String patientId;
    @NotBlank(message = "fullName is required")
    private String fullName;
    @NotBlank(message = "gender is required")
    private String gender;
    @NotBlank(message = "passportNumber is required")
    private String passportNumber;

    public String getPatientId() { return patientId; }
    public void setPatientId(String v) { this.patientId = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getGender() { return gender; }
    public void setGender(String v) { this.gender = v; }
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String v) { this.passportNumber = v; }
}