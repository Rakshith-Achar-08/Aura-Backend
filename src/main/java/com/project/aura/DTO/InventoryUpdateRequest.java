package com.project.aura.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdateRequest {

    private Integer hospitalId;
    private Integer itemId;
    private Integer quantity;
    private Integer reorderThreshold;
}
