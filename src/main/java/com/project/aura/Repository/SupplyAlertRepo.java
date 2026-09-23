package com.project.aura.Repository;

import com.project.aura.Entity.SupplyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyAlertRepo extends JpaRepository<SupplyAlert, Integer> {

    List<SupplyAlert> findByHospital_HospitalIdOrderByCreatedAtDesc(Integer hospitalId);

    List<SupplyAlert> findByStatusOrderByCreatedAtDesc(SupplyAlert.AlertStatus status);

    List<SupplyAlert> findByAlertTypeOrderByCreatedAtDesc(SupplyAlert.AlertType alertType);

    List<SupplyAlert> findByHospital_HospitalIdAndStatusOrderByCreatedAtDesc(
            Integer hospitalId, SupplyAlert.AlertStatus status);
}
