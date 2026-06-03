package com.albert.patientsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiagnosisResult {
    public Binary binary;
    public Subtype subtype;

    public static class Binary {
        public String label;
        @JsonProperty("probability_malignant")
        public Double probabilityMalignant;
        public Double threshold;
    }
    public static class Subtype {
        public String label;
        public Double confidence;
    }
}