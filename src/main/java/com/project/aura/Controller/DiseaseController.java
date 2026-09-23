package com.project.aura.Controller;

import com.project.aura.DTO.DiseaseDTO;
import com.project.aura.Service.DiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    /** GET /api/diseases — list all diseases */
    @GetMapping
    public ResponseEntity<List<DiseaseDTO>> getAllDiseases() {
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }

    /** GET /api/diseases/{id} — get disease by ID */
    @GetMapping("/{id}")
    public ResponseEntity<DiseaseDTO> getDiseaseById(@PathVariable Integer id) {
        return ResponseEntity.ok(diseaseService.getDiseaseById(id));
    }

    /** POST /api/diseases — create a new disease (admin only) */
    @PostMapping
    public ResponseEntity<DiseaseDTO> createDisease(@RequestBody DiseaseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diseaseService.createDisease(dto));
    }

    /** PUT /api/diseases/{id} — update a disease */
    @PutMapping("/{id}")
    public ResponseEntity<DiseaseDTO> updateDisease(@PathVariable Integer id, @RequestBody DiseaseDTO dto) {
        return ResponseEntity.ok(diseaseService.updateDisease(id, dto));
    }

    /** DELETE /api/diseases/{id} — delete a disease */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDisease(@PathVariable Integer id) {
        diseaseService.deleteDisease(id);
        return ResponseEntity.ok("Disease deleted successfully");
    }
}
