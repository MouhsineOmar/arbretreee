package com.fsoteam.ml.decisiontree.controller;

import com.fsoteam.ml.decisiontree.model.DataUploadResponse;
import com.fsoteam.ml.decisiontree.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {

    @Autowired
    private DataService dataService;

    @PostMapping("/upload")
    public ResponseEntity<DataUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new DataUploadResponse(false, "Aucun fichier sélectionné"));
        }

        DataUploadResponse response = dataService.uploadAndProcessFile(file);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<DataUploadResponse> getDataStatus() {
        if (dataService.hasDataset()) {
            DataUploadResponse response = new DataUploadResponse(true, "Dataset chargé");
            // Vous pouvez ajouter plus d'informations sur le dataset ici si nécessaire
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(new DataUploadResponse(false, "Aucun dataset chargé"));
        }
    }
}

