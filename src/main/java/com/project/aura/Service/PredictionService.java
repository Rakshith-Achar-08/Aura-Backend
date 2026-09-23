package com.project.aura.Service;

import com.project.aura.DTO.DiseaseDTO;
import com.project.aura.DTO.PredictRequest;
import com.project.aura.DTO.PredictResponse;
import com.project.aura.DTO.SymptomDTO;
import com.project.aura.Entity.Disease;
import com.project.aura.Entity.PredictionRecord;
import com.project.aura.Entity.Symptom;
import com.project.aura.Entity.Users;
import com.project.aura.Exception.ResourceNotFoundException;
import com.project.aura.Repository.DiseaseRepo;
import com.project.aura.Repository.PredictionRecordRepo;
import com.project.aura.Repository.SymptomRepo;
import com.project.aura.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PredictionService — stores prediction results sent from the frontend
 * (which calls the separate Python ML service for actual inference).
 *
 * Flow:
 *   1. User selects symptoms in React UI
 *   2. React calls Python ML API → gets { predictedDiseaseId, confidenceScore }
 *   3. React calls POST /api/predict with all data → this service persists it
 *   4. Stored predictions are retrievable for history/analytics
 */
@Service
public class PredictionService {

    @Autowired
    private PredictionRecordRepo predictionRecordRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DiseaseRepo diseaseRepo;

    @Autowired
    private SymptomRepo symptomRepo;

    /**
     * Store a prediction result (received from Python ML service via frontend).
     */
    public PredictResponse storePrediction(PredictRequest request) {

        Users user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        Disease disease = diseaseRepo.findById(request.getPredictedDiseaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found: " + request.getPredictedDiseaseId()));

        List<Symptom> symptoms = symptomRepo.findAllById(request.getSymptomIds());

        PredictionRecord record = PredictionRecord.builder()
                .user(user)
                .predictedDisease(disease)
                .confidenceScore(request.getConfidenceScore())
                .symptoms(symptoms)
                .build();

        return toResponse(predictionRecordRepo.save(record));
    }

    /**
     * Get all predictions for a specific user, newest first.
     */
    public List<PredictResponse> getUserPredictions(Integer userId) {
        return predictionRecordRepo
                .findByUser_UseridOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single prediction record by ID.
     */
    public PredictResponse getPredictionById(Integer predictionId) {
        PredictionRecord record = predictionRecordRepo.findById(predictionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prediction not found: " + predictionId));
        return toResponse(record);
    }

    public void deletePrediction(Integer predictionId) {
        if (!predictionRecordRepo.existsById(predictionId)) {
            throw new ResourceNotFoundException("Prediction not found: " + predictionId);
        }
        predictionRecordRepo.deleteById(predictionId);
    }

    // ── Mapper ──────────────────────────────────────────────────────────────────

    private PredictResponse toResponse(PredictionRecord r) {
        List<SymptomDTO> symptomDTOs = r.getSymptoms().stream()
                .map(s -> SymptomDTO.builder()
                        .symptomId(s.getSymptomId())
                        .name(s.getName())
                        .build())
                .collect(Collectors.toList());

        DiseaseDTO diseaseDTO = DiseaseDTO.builder()
                .diseaseId(r.getPredictedDisease().getDiseaseId())
                .name(r.getPredictedDisease().getName())
                .description(r.getPredictedDisease().getDescription())
                .precautions(r.getPredictedDisease().getPrecautions())
                .build();

        return PredictResponse.builder()
                .predictionId(r.getPredictionId())
                .userId(r.getUser().getUserid())
                .predictedDisease(diseaseDTO)
                .confidenceScore(r.getConfidenceScore())
                .symptoms(symptomDTOs)
                .createdAt(r.getCreatedAt())
                .build();
    }
}
