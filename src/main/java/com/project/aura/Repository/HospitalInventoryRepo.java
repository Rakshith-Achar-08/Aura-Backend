package com.project.aura.Repository;

import com.project.aura.Entity.HospitalInventory;
import com.project.aura.Entity.HospitalInventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalInventoryRepo extends JpaRepository<HospitalInventory, HospitalInventoryId> {

    @Query("SELECT hi FROM HospitalInventory hi WHERE hi.hospital.hospitalId = :hospitalId")
    List<HospitalInventory> findByHospitalId(@Param("hospitalId") Integer hospitalId);

    @Query("SELECT hi FROM HospitalInventory hi WHERE hi.hospital.hospitalId = :hospitalId AND hi.quantity <= hi.reorderThreshold")
    List<HospitalInventory> findLowStockByHospital(@Param("hospitalId") Integer hospitalId);

    @Query("SELECT hi FROM HospitalInventory hi WHERE hi.quantity <= hi.reorderThreshold")
    List<HospitalInventory> findAllLowStock();

    @Query("SELECT hi FROM HospitalInventory hi WHERE hi.quantity = 0")
    List<HospitalInventory> findOutOfStock();
}
