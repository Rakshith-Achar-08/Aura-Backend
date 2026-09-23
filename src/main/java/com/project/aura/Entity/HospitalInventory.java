package com.project.aura.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hospital_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalInventory {

    @EmbeddedId
    private HospitalInventoryId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("hospitalId")
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private InventoryItem item;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "reorder_threshold")
    private Integer reorderThreshold;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
