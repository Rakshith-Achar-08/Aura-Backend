package com.project.aura.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiseaseDTO {

    private Integer diseaseId;
    private String name;
    private String description;
    private String precautions;
}
