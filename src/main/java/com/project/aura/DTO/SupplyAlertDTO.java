package com.project.aura.DTO;

import com.project.aura.Entity.SupplyAlert;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplyAlertDTO {

    private Integer supplyAlertId;
    private Integer hospitalId;
    private String hospitalName;
    private Integer itemId;
    private String itemName;
    private SupplyAlert.AlertType alertType;
    private String message;
    private SupplyAlert.AlertStatus status;
    private LocalDateTime createdAt;
}
