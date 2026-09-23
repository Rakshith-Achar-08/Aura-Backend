package com.project.aura.Controller;

import com.project.aura.DTO.InventoryStatusDTO;
import com.project.aura.DTO.InventoryUpdateRequest;
import com.project.aura.Entity.InventoryItem;
import com.project.aura.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // ── Inventory Items (Master List) ────────────────────────────────────────────

    /** GET /api/inventory/items — list all inventory item types */
    @GetMapping("/items")
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    /** POST /api/inventory/items — add a new item type to master list */
    @PostMapping("/items")
    public ResponseEntity<InventoryItem> createItem(@RequestBody InventoryItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createItem(item));
    }

    // ── Hospital Stock ───────────────────────────────────────────────────────────

    /** GET /api/inventory/hospital/{hospitalId} — get all stock for a hospital */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<InventoryStatusDTO>> getHospitalInventory(@PathVariable Integer hospitalId) {
        return ResponseEntity.ok(inventoryService.getHospitalInventory(hospitalId));
    }

    /** GET /api/inventory/hospital/{hospitalId}/low-stock — get items below reorder threshold */
    @GetMapping("/hospital/{hospitalId}/low-stock")
    public ResponseEntity<List<InventoryStatusDTO>> getLowStock(@PathVariable Integer hospitalId) {
        return ResponseEntity.ok(inventoryService.getLowStockItems(hospitalId));
    }

    /** GET /api/inventory/low-stock — global low-stock view across all hospitals */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryStatusDTO>> getAllLowStock() {
        return ResponseEntity.ok(inventoryService.getAllLowStock());
    }

    /**
     * POST /api/inventory/update — create or update stock for a hospital-item pair.
     * Body: { "hospitalId": 1, "itemId": 2, "quantity": 100, "reorderThreshold": 20 }
     */
    @PostMapping("/update")
    public ResponseEntity<InventoryStatusDTO> upsertInventory(@RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.upsertInventory(request));
    }
}
