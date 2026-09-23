package com.project.aura.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "supply_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supply_alert_id")
    private Integer supplyAlertId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private InventoryItem item;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type")
    private AlertType alertType;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AlertStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum AlertType {
        LOW_STOCK,
        CRITICAL_STOCK,
        OUT_OF_STOCK,
        PANDEMIC_RESTOCK
    }

    public enum AlertStatus {
        PENDING,
        IN_PROGRESS,
        RESOLVED
    }
}
