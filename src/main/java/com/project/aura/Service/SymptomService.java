package com.project.aura.Service;

import com.project.aura.DTO.SymptomDTO;
import com.project.aura.Entity.Symptom;
import com.project.aura.Exception.DuplicateResourceException;
import com.project.aura.Exception.ResourceNotFoundException;
import com.project.aura.Repository.SymptomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SymptomService {

    @Autowired
    private SymptomRepo symptomRepo;

    public List<SymptomDTO> getAllSymptoms() {
        return symptomRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SymptomDTO getSymptomById(Integer id) {
        return toDTO(symptomRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Symptom not found with id: " + id)));
    }

    public List<SymptomDTO> searchSymptoms(String keyword) {
        return symptomRepo.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SymptomDTO createSymptom(SymptomDTO dto) {
        if (symptomRepo.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Symptom already exists: " + dto.getName());
        }
        Symptom symptom = Symptom.builder()
                .name(dto.getName())
                .build();
        return toDTO(symptomRepo.save(symptom));
    }

    public SymptomDTO updateSymptom(Integer id, SymptomDTO dto) {
        Symptom symptom = symptomRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Symptom not found with id: " + id));
        symptom.setName(dto.getName());
        return toDTO(symptomRepo.save(symptom));
    }

    public void deleteSymptom(Integer id) {
        if (!symptomRepo.existsById(id)) {
            throw new ResourceNotFoundException("Symptom not found with id: " + id);
        }
        symptomRepo.deleteById(id);
    }

    // ── Mapper ──────────────────────────────────────────────────────────────────

    public SymptomDTO toDTO(Symptom s) {
        return SymptomDTO.builder()
                .symptomId(s.getSymptomId())
                .name(s.getName())
                .build();
    }
}
