package com.project.aura.Controller;

import com.project.aura.DTO.OutbreakReportDTO;
import com.project.aura.DTO.SupplyAlertDTO;
import com.project.aura.Service.SupplyChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supply")
public class SupplyChainController {

    @Autowired
    private SupplyChainService supplyChainService;

    // ── Outbreak Reports ─────────────────────────────────────────────────────────

    /**
     * POST /api/supply/outbreak
     * File an outbreak report. If cases ≥ 50, automatically creates supply alerts.
     * If cases ≥ 500, triggers regional supply chain activation across all hospitals.
     */
    @PostMapping("/outbreak")
    public ResponseEntity<OutbreakReportDTO> fileOutbreakReport(@RequestBody OutbreakReportDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplyChainService.fileOutbreakReport(dto));
    }

    /** GET /api/supply/outbreaks — list all outbreak reports */
    @GetMapping("/outbreaks")
    public ResponseEntity<List<OutbreakReportDTO>> getAllOutbreaks() {
        return ResponseEntity.ok(supplyChainService.getAllOutbreakReports());
    }

    /** GET /api/supply/outbreaks/disease/{diseaseId} */
    @GetMapping("/outbreaks/disease/{diseaseId}")
    public ResponseEntity<List<OutbreakReportDTO>> getOutbreaksByDisease(@PathVariable Integer diseaseId) {
        return ResponseEntity.ok(supplyChainService.getOutbreaksByDisease(diseaseId));
    }

    /** GET /api/supply/outbreaks/hospital/{hospitalId} */
    @GetMapping("/outbreaks/hospital/{hospitalId}")
    public ResponseEntity<List<OutbreakReportDTO>> getOutbreaksByHospital(@PathVariable Integer hospitalId) {
        return ResponseEntity.ok(supplyChainService.getOutbreaksByHospital(hospitalId));
    }

    // ── Supply Alerts ────────────────────────────────────────────────────────────

    /** GET /api/supply/alerts — list all supply alerts */
    @GetMapping("/alerts")
    public ResponseEntity<List<SupplyAlertDTO>> getAllSupplyAlerts() {
        return ResponseEntity.ok(supplyChainService.getAllSupplyAlerts());
    }

    /** GET /api/supply/alerts/hospital/{hospitalId} */
    @GetMapping("/alerts/hospital/{hospitalId}")
    public ResponseEntity<List<SupplyAlertDTO>> getAlertsByHospital(@PathVariable Integer hospitalId) {
        return ResponseEntity.ok(supplyChainService.getSupplyAlertsByHospital(hospitalId));
    }

    /** GET /api/supply/alerts/status?status=PENDING */
    @GetMapping("/alerts/status")
    public ResponseEntity<List<SupplyAlertDTO>> getAlertsByStatus(@RequestParam String status) {
        return ResponseEntity.ok(supplyChainService.getSupplyAlertsByStatus(status));
    }

    /**
     * PATCH /api/supply/alerts/{alertId}/status?status=IN_PROGRESS
     * Update a supply alert status (PENDING → IN_PROGRESS → RESOLVED).
     */
    @PatchMapping("/alerts/{alertId}/status")
    public ResponseEntity<SupplyAlertDTO> updateAlertStatus(
            @PathVariable Integer alertId,
            @RequestParam String status) {
        return ResponseEntity.ok(supplyChainService.updateSupplyAlertStatus(alertId, status));
    }
}
