package com.project.aura.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prediction_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private Integer predictionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "predicted_disease_id", nullable = false)
    private Disease predictedDisease;

    @Column(name = "confidence_score")
    private Float confidenceScore;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Maps to the PREDICTION_SYMPTOMS junction table (prediction_id PK + symptom_id PK).
     * Using @ManyToMany with @JoinTable so JPA manages the composite-key junction table.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "prediction_symptoms",
            joinColumns = @JoinColumn(name = "prediction_id"),
            inverseJoinColumns = @JoinColumn(name = "symptom_id")
    )
    private List<Symptom> symptoms;
}
