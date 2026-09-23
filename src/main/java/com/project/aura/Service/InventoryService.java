package com.project.aura.Service;

import com.project.aura.DTO.InventoryStatusDTO;
import com.project.aura.DTO.InventoryUpdateRequest;
import com.project.aura.Entity.Hospital;
import com.project.aura.Entity.HospitalInventory;
import com.project.aura.Entity.HospitalInventoryId;
import com.project.aura.Entity.InventoryItem;
import com.project.aura.Exception.ResourceNotFoundException;
import com.project.aura.Repository.HospitalInventoryRepo;
import com.project.aura.Repository.HospitalRepo;
import com.project.aura.Repository.InventoryItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private HospitalInventoryRepo hospitalInventoryRepo;

    @Autowired
    private HospitalRepo hospitalRepo;

    @Autowired
    private InventoryItemRepo inventoryItemRepo;

    public List<InventoryStatusDTO> getHospitalInventory(Integer hospitalId) {
        if (!hospitalRepo.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found: " + hospitalId);
        }
        return hospitalInventoryRepo.findByHospitalId(hospitalId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryStatusDTO> getLowStockItems(Integer hospitalId) {
        return hospitalInventoryRepo.findLowStockByHospital(hospitalId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryStatusDTO> getAllLowStock() {
        return hospitalInventoryRepo.findAllLowStock().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create or update inventory entry for a hospital-item pair.
     * This is an upsert: if the record exists, update it; otherwise create it.
     */
    public InventoryStatusDTO upsertInventory(InventoryUpdateRequest request) {

        Hospital hospital = hospitalRepo.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found: " + request.getHospitalId()));

        InventoryItem item = inventoryItemRepo.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + request.getItemId()));

        HospitalInventoryId id = new HospitalInventoryId(request.getHospitalId(), request.getItemId());

        HospitalInventory inventory = hospitalInventoryRepo.findById(id)
                .orElse(HospitalInventory.builder()
                        .id(id)
                        .hospital(hospital)
                        .item(item)
                        .build());

        inventory.setQuantity(request.getQuantity());
        inventory.setReorderThreshold(request.getReorderThreshold());
        inventory.setLastUpdated(LocalDateTime.now());

        return toDTO(hospitalInventoryRepo.save(inventory));
    }

    // ── Inventory Items (Master List) ────────────────────────────────────────────

    public List<InventoryItem> getAllItems() {
        return inventoryItemRepo.findAll();
    }

    public InventoryItem createItem(InventoryItem item) {
        if (inventoryItemRepo.existsByName(item.getName())) {
            throw new com.project.aura.Exception.DuplicateResourceException("Item already exists: " + item.getName());
        }
        return inventoryItemRepo.save(item);
    }

    // ── Mapper ──────────────────────────────────────────────────────────────────

    private InventoryStatusDTO toDTO(HospitalInventory inv) {
        return InventoryStatusDTO.builder()
                .hospitalId(inv.getHospital().getHospitalId())
                .hospitalName(inv.getHospital().getName())
                .itemId(inv.getItem().getItemId())
                .itemName(inv.getItem().getName())
                .category(inv.getItem().getCategory())
                .unit(inv.getItem().getUnit())
                .quantity(inv.getQuantity())
                .reorderThreshold(inv.getReorderThreshold())
                .isLowStock(inv.getQuantity() != null && inv.getReorderThreshold() != null
                        && inv.getQuantity() <= inv.getReorderThreshold())
                .lastUpdated(inv.getLastUpdated())
                .build();
    }
}
