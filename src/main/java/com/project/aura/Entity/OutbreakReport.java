package com.project.aura.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "outbreak_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutbreakReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disease_id")
    private Disease disease;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(name = "reported_cases")
    private Integer reportedCases;

    @Column(name = "report_date")
    private LocalDate reportDate;
}
