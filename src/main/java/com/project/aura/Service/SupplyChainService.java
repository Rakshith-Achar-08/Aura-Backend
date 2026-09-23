package com.project.aura.Service;

import com.project.aura.DTO.OutbreakReportDTO;
import com.project.aura.DTO.SupplyAlertDTO;
import com.project.aura.Entity.*;
import com.project.aura.Exception.ResourceNotFoundException;
import com.project.aura.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SupplyChainService — the AI-driven medical supply chain module.
 *
 * Core intelligence:
 *  1. When an outbreak report is filed with cases ≥ PANDEMIC_THRESHOLD,
 *     this service automatically scans all low-stock inventory at the affected
 *     hospital and creates PANDEMIC_RESTOCK supply alerts.
 *  2. If the outbreak is very large (≥ CRITICAL_THRESHOLD), it also alerts
 *     all other hospitals with low stock — simulating a regional supply response.
 *  3. Duplicate alerts are prevented (only creates if no PENDING alert exists
 *     for the same hospital-item pair).
 */
@Service
public class SupplyChainService {

    /** Cases at or above this threshold trigger automatic supply alerts for the affected hospital. */
    private static final int PANDEMIC_THRESHOLD = 50;

    /** Cases at or above this threshold also alert neighbouring hospitals regionally. */
    private static final int CRITICAL_THRESHOLD = 500;

    @Autowired
    private OutbreakReportRepo outbreakReportRepo;

    @Autowired
    private HospitalInventoryRepo hospitalInventoryRepo;

    @Autowired
    private SupplyAlertRepo supplyAlertRepo;

    @Autowired
    private HospitalRepo hospitalRepo;

    @Autowired
    private DiseaseRepo diseaseRepo;

    // ── Outbreak Report Management ───────────────────────────────────────────────

    @Transactional
    public OutbreakReportDTO fileOutbreakReport(OutbreakReportDTO dto) {

        Hospital hospital = hospitalRepo.findById(dto.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found: " + dto.getHospitalId()));

        Disease disease = diseaseRepo.findById(dto.getDiseaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found: " + dto.getDiseaseId()));

        OutbreakReport report = OutbreakReport.builder()
                .hospital(hospital)
                .disease(disease)
                .reportedCases(dto.getReportedCases())
                .reportDate(dto.getReportDate() != null ? dto.getReportDate() : LocalDate.now())
                .build();

        OutbreakReport saved = outbreakReportRepo.save(report);

        // ── Auto-trigger supply alerts if cases cross thresholds ─────────────────
        if (dto.getReportedCases() >= PANDEMIC_THRESHOLD) {
            triggerSupplyAlertsForHospital(hospital, disease, dto.getReportedCases());
        }

        if (dto.getReportedCases() >= CRITICAL_THRESHOLD) {
            // Critical outbreak — alert all hospitals with low stock
            triggerGlobalSupplyAlerts(disease, dto.getReportedCases());
        }

        return toOutbreakDTO(saved);
    }

    public List<OutbreakReportDTO> getAllOutbreakReports() {
        return outbreakReportRepo.findAllByOrderByReportDateDesc().stream()
                .map(this::toOutbreakDTO)
                .collect(Collectors.toList());
    }

    public List<OutbreakReportDTO> getOutbreaksByDisease(Integer diseaseId) {
        return outbreakReportRepo.findByDisease_DiseaseIdOrderByReportDateDesc(diseaseId).stream()
                .map(this::toOutbreakDTO)
                .collect(Collectors.toList());
    }

    public List<OutbreakReportDTO> getOutbreaksByHospital(Integer hospitalId) {
        return outbreakReportRepo.findByHospital_HospitalIdOrderByReportDateDesc(hospitalId).stream()
                .map(this::toOutbreakDTO)
                .collect(Collectors.toList());
    }

    // ── Supply Alert Management ──────────────────────────────────────────────────

    public List<SupplyAlertDTO> getAllSupplyAlerts() {
        return supplyAlertRepo.findAll().stream()
                .map(this::toSupplyDTO)
                .collect(Collectors.toList());
    }

    public List<SupplyAlertDTO> getSupplyAlertsByHospital(Integer hospitalId) {
        return supplyAlertRepo.findByHospital_HospitalIdOrderByCreatedAtDesc(hospitalId).stream()
                .map(this::toSupplyDTO)
                .collect(Collectors.toList());
    }

    public List<SupplyAlertDTO> getSupplyAlertsByStatus(String status) {
        try {
            SupplyAlert.AlertStatus alertStatus = SupplyAlert.AlertStatus.valueOf(status.toUpperCase());
            return supplyAlertRepo.findByStatusOrderByCreatedAtDesc(alertStatus).stream()
                    .map(this::toSupplyDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Use: PENDING, IN_PROGRESS, or RESOLVED");
        }
    }

    public SupplyAlertDTO updateSupplyAlertStatus(Integer alertId, String status) {
        SupplyAlert alert = supplyAlertRepo.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Supply alert not found: " + alertId));
        try {
            alert.setStatus(SupplyAlert.AlertStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Use: PENDING, IN_PROGRESS, or RESOLVED");
        }
        return toSupplyDTO(supplyAlertRepo.save(alert));
    }

    // ── Private: Auto-Alert Logic ────────────────────────────────────────────────

    /**
     * Scans the given hospital's low-stock inventory and creates PANDEMIC_RESTOCK alerts.
     * Skips items that already have a PENDING alert.
     */
    private void triggerSupplyAlertsForHospital(Hospital hospital, Disease disease, int cases) {
        List<HospitalInventory> lowStockItems =
                hospitalInventoryRepo.findLowStockByHospital(hospital.getHospitalId());

        // Get existing PENDING alert item IDs to avoid duplicates
        List<Integer> pendingItemIds = supplyAlertRepo
                .findByHospital_HospitalIdAndStatusOrderByCreatedAtDesc(
                        hospital.getHospitalId(), SupplyAlert.AlertStatus.PENDING)
                .stream()
                .map(a -> a.getItem().getItemId())
                .collect(Collectors.toList());

        for (HospitalInventory inv : lowStockItems) {
            if (!pendingItemIds.contains(inv.getItem().getItemId())) {
                SupplyAlert alert = SupplyAlert.builder()
                        .hospital(hospital)
                        .item(inv.getItem())
                        .alertType(SupplyAlert.AlertType.PANDEMIC_RESTOCK)
                        .message(String.format(
                                "🚨 PANDEMIC ALERT: %d cases of '%s' reported at '%s'. " +
                                "Item '%s' is below reorder threshold (%d units remaining, threshold: %d). " +
                                "IMMEDIATE RESTOCKING REQUIRED.",
                                cases, disease.getName(), hospital.getName(),
                                inv.getItem().getName(), inv.getQuantity(), inv.getReorderThreshold()))
                        .status(SupplyAlert.AlertStatus.PENDING)
                        .build();
                supplyAlertRepo.save(alert);
            }
        }
    }

    /**
     * For critical-scale outbreaks, create alerts across ALL hospitals with low stock.
     */
    private void triggerGlobalSupplyAlerts(Disease disease, int cases) {
        List<HospitalInventory> globalLowStock = hospitalInventoryRepo.findAllLowStock();

        for (HospitalInventory inv : globalLowStock) {
            Hospital hospital = inv.getHospital();

            List<Integer> pendingItemIds = supplyAlertRepo
                    .findByHospital_HospitalIdAndStatusOrderByCreatedAtDesc(
                            hospital.getHospitalId(), SupplyAlert.AlertStatus.PENDING)
                    .stream()
                    .map(a -> a.getItem().getItemId())
                    .collect(Collectors.toList());

            if (!pendingItemIds.contains(inv.getItem().getItemId())) {
                SupplyAlert alert = SupplyAlert.builder()
                        .hospital(hospital)
                        .item(inv.getItem())
                        .alertType(SupplyAlert.AlertType.CRITICAL_STOCK)
                        .message(String.format(
                                "🔴 CRITICAL OUTBREAK ALERT: %d cases of '%s' detected regionally. " +
                                "Hospital '%s' has low stock of '%s' (%d units). " +
                                "Regional supply chain activation required.",
                                cases, disease.getName(), hospital.getName(),
                                inv.getItem().getName(), inv.getQuantity()))
                        .status(SupplyAlert.AlertStatus.PENDING)
                        .build();
                supplyAlertRepo.save(alert);
            }
        }
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    private OutbreakReportDTO toOutbreakDTO(OutbreakReport r) {
        return OutbreakReportDTO.builder()
                .reportId(r.getReportId())
                .diseaseId(r.getDisease().getDiseaseId())
                .diseaseName(r.getDisease().getName())
                .hospitalId(r.getHospital().getHospitalId())
                .hospitalName(r.getHospital().getName())
                .reportedCases(r.getReportedCases())
                .reportDate(r.getReportDate())
                .build();
    }

    private SupplyAlertDTO toSupplyDTO(SupplyAlert a) {
        return SupplyAlertDTO.builder()
                .supplyAlertId(a.getSupplyAlertId())
                .hospitalId(a.getHospital() != null ? a.getHospital().getHospitalId() : null)
                .hospitalName(a.getHospital() != null ? a.getHospital().getName() : null)
                .itemId(a.getItem() != null ? a.getItem().getItemId() : null)
                .itemName(a.getItem() != null ? a.getItem().getName() : null)
                .alertType(a.getAlertType())
                .message(a.getMessage())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
