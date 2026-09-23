package com.project.aura.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PredictRequest {

    private Integer userId;
    /** IDs of symptoms selected by the user (sent to Python ML, result sent back here) */
    private List<Integer> symptomIds;
    /** Disease ID predicted by the Python ML service */
    private Integer predictedDiseaseId;
    /** Confidence score (0.0 - 1.0) returned by the Python ML service */
    private Float confidenceScore;
}
