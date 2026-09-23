package com.project.aura.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite primary key for HOSPITAL_INVENTORY table.
 * Maps (hospital_id, item_id) composite PK from the ER diagram.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HospitalInventoryId implements Serializable {

    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Column(name = "item_id")
    private Integer itemId;
}
