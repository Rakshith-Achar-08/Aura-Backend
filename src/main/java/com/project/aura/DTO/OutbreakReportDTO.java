package com.project.aura.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutbreakReportDTO {

    private Integer reportId;
    private Integer diseaseId;
    private String diseaseName;
    private Integer hospitalId;
    private String hospitalName;
    private Integer reportedCases;
    private LocalDate reportDate;
}
