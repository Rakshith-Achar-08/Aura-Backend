package com.project.aura.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "symptoms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "symptom_id")
    private Integer symptomId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
