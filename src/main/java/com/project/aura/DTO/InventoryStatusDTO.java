package com.project.aura.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryStatusDTO {

    private Integer hospitalId;
    private String hospitalName;
    private Integer itemId;
    private String itemName;
    private String category;
    private String unit;
    private Integer quantity;
    private Integer reorderThreshold;
    private Boolean isLowStock;
    private LocalDateTime lastUpdated;
}
