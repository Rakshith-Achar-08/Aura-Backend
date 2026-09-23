package com.project.aura.Controller;

import com.project.aura.DTO.PredictRequest;
import com.project.aura.DTO.PredictResponse;
import com.project.aura.Service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predict")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    /**
     * POST /api/predict
     * Frontend sends: userId + symptomIds (selected by user) + predictedDiseaseId + confidenceScore
     * (disease and confidence come from the Python ML service called by the frontend)
     */
    @PostMapping
    public ResponseEntity<PredictResponse> storePrediction(@RequestBody PredictRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(predictionService.storePrediction(request));
    }

    /** GET /api/predict/history/{userId} — get all predictions for a user */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<PredictResponse>> getUserPredictions(@PathVariable Integer userId) {
        return ResponseEntity.ok(predictionService.getUserPredictions(userId));
    }

    /** GET /api/predict/{id} — get a specific prediction record */
    @GetMapping("/{id}")
    public ResponseEntity<PredictResponse> getPredictionById(@PathVariable Integer id) {
        return ResponseEntity.ok(predictionService.getPredictionById(id));
    }

    /** DELETE /api/predict/{id} — delete a prediction */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePrediction(@PathVariable Integer id) {
        predictionService.deletePrediction(id);
        return ResponseEntity.ok("Prediction deleted successfully");
    }
}
