package com.fsoteam.ml.decisiontree.controller;

import com.fsoteam.ml.decisiontree.model.TrainingRequest;
import com.fsoteam.ml.decisiontree.model.TrainingResponse;
import com.fsoteam.ml.decisiontree.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model")
@CrossOrigin(origins = "*")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @PostMapping("/train")
    public ResponseEntity<TrainingResponse> trainModel(@RequestBody TrainingRequest request) {
        // Validation des paramètres
        if (request.getAlgorithm() == null || request.getAlgorithm().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new TrainingResponse(false, "L'algorithme doit être spécifié"));
        }

        String algorithm = request.getAlgorithm().toUpperCase();
        if (!algorithm.equals("ID3") && !algorithm.equals("CART") && !algorithm.equals("RANDOMFOREST")) {
            return ResponseEntity.badRequest()
                .body(new TrainingResponse(false, "Algorithme non supporté. Utilisez ID3, CART ou RandomForest"));
        }

        if (algorithm.equals("RANDOMFOREST") && request.getNumberOfTrees() <= 0) {
            return ResponseEntity.badRequest()
                .body(new TrainingResponse(false, "Le nombre d'arbres doit être positif pour Random Forest"));
        }

        TrainingResponse response = modelService.trainModel(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<TrainingResponse> getModelStatus() {
        if (modelService.hasTrainedModel()) {
            return ResponseEntity.ok(new TrainingResponse(true, "Modèle entraîné disponible"));
        } else {
            return ResponseEntity.ok(new TrainingResponse(false, "Aucun modèle entraîné"));
        }
    }

    @GetMapping("/algorithms")
    public ResponseEntity<String[]> getSupportedAlgorithms() {
        return ResponseEntity.ok(new String[]{"ID3", "CART", "RandomForest"});
    }
}

