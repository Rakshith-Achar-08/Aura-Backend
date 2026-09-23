package com.project.aura.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PredictResponse {

    private Integer predictionId;
    private Integer userId;
    private DiseaseDTO predictedDisease;
    private Float confidenceScore;
    private List<SymptomDTO> symptoms;
    private LocalDateTime createdAt;
}
