package com.project.aura.Service;

import com.project.aura.DTO.DiseaseDTO;
import com.project.aura.Entity.Disease;
import com.project.aura.Exception.DuplicateResourceException;
import com.project.aura.Exception.ResourceNotFoundException;
import com.project.aura.Repository.DiseaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiseaseService {

    @Autowired
    private DiseaseRepo diseaseRepo;

    public List<DiseaseDTO> getAllDiseases() {
        return diseaseRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DiseaseDTO getDiseaseById(Integer id) {
        return toDTO(diseaseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with id: " + id)));
    }

    public DiseaseDTO createDisease(DiseaseDTO dto) {
        if (diseaseRepo.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Disease already exists: " + dto.getName());
        }
        Disease disease = Disease.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .precautions(dto.getPrecautions())
                .build();
        return toDTO(diseaseRepo.save(disease));
    }

    public DiseaseDTO updateDisease(Integer id, DiseaseDTO dto) {
        Disease disease = diseaseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with id: " + id));
        disease.setName(dto.getName());
        disease.setDescription(dto.getDescription());
        disease.setPrecautions(dto.getPrecautions());
        return toDTO(diseaseRepo.save(disease));
    }

    public void deleteDisease(Integer id) {
        if (!diseaseRepo.existsById(id)) {
            throw new ResourceNotFoundException("Disease not found with id: " + id);
        }
        diseaseRepo.deleteById(id);
    }

    // ── Mapper ──────────────────────────────────────────────────────────────────

    public DiseaseDTO toDTO(Disease d) {
        return DiseaseDTO.builder()
                .diseaseId(d.getDiseaseId())
                .name(d.getName())
                .description(d.getDescription())
                .precautions(d.getPrecautions())
                .build();
    }
}
