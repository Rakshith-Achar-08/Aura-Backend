package com.project.aura.Controller;

import com.project.aura.DTO.SymptomDTO;
import com.project.aura.Service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/symptoms")
public class SymptomController {

    @Autowired
    private SymptomService symptomService;

    /** GET /api/symptoms — list all symptoms */
    @GetMapping
    public ResponseEntity<List<SymptomDTO>> getAllSymptoms() {
        return ResponseEntity.ok(symptomService.getAllSymptoms());
    }

    /** GET /api/symptoms/{id} — get symptom by ID */
    @GetMapping("/{id}")
    public ResponseEntity<SymptomDTO> getSymptomById(@PathVariable Integer id) {
        return ResponseEntity.ok(symptomService.getSymptomById(id));
    }

    /** GET /api/symptoms/search?q=fever — search symptoms by keyword (for autocomplete) */
    @GetMapping("/search")
    public ResponseEntity<List<SymptomDTO>> searchSymptoms(@RequestParam("q") String keyword) {
        return ResponseEntity.ok(symptomService.searchSymptoms(keyword));
    }

    /** POST /api/symptoms — create a new symptom */
    @PostMapping
    public ResponseEntity<SymptomDTO> createSymptom(@RequestBody SymptomDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(symptomService.createSymptom(dto));
    }

    /** PUT /api/symptoms/{id} — update a symptom */
    @PutMapping("/{id}")
    public ResponseEntity<SymptomDTO> updateSymptom(@PathVariable Integer id, @RequestBody SymptomDTO dto) {
        return ResponseEntity.ok(symptomService.updateSymptom(id, dto));
    }

    /** DELETE /api/symptoms/{id} — delete a symptom */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSymptom(@PathVariable Integer id) {
        symptomService.deleteSymptom(id);
        return ResponseEntity.ok("Symptom deleted successfully");
    }
}
