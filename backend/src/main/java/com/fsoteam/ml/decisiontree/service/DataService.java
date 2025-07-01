package com.fsoteam.ml.decisiontree.service;

import com.fsoteam.ml.decisiontree.decisionTree.Attribute;
import com.fsoteam.ml.decisiontree.decisionTree.Branch;
import com.fsoteam.ml.decisiontree.model.*;
import com.fsoteam.ml.decisiontree.utils.CustomFileReader;
import com.fsoteam.ml.decisiontree.utils.DatasetInitializer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataService {
    
    private DatasetInitializer currentDataset;
    private final String uploadDir = "/tmp/uploads/";

    public DataService() {
        // Créer le répertoire d'upload s'il n'existe pas
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du répertoire d'upload: " + e.getMessage());
        }
    }

    public DataUploadResponse uploadAndProcessFile(MultipartFile file) {
        try {
            // Vérifier le type de fichier
            if (!file.getOriginalFilename().toLowerCase().endsWith(".arff")) {
                return new DataUploadResponse(false, "Seuls les fichiers ARFF sont supportés");
            }

            // Sauvegarder le fichier temporairement
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());

            // Traiter le fichier ARFF
            DatasetInitializer datasetInitializer = processArffFile(filePath.toString());
            this.currentDataset = datasetInitializer;

            // Créer la réponse
            DataUploadResponse response = new DataUploadResponse(true, "Fichier traité avec succès");
            response.setAttributeNames(
                datasetInitializer.getAttributes().stream()
                    .map(Attribute::getAttributeName)
                    .collect(Collectors.toList())
            );
            response.setClassName(datasetInitializer.getClassName());
            response.setInstanceCount(datasetInitializer.getInstanceData().size());
            
            // Créer un aperçu des données (premières 10 instances)
            List<DataUploadResponse.InstanceData> preview = datasetInitializer.getInstanceData()
                .stream()
                .limit(10)
                .map(instance -> new DataUploadResponse.InstanceData(
                    instance.getInstanceId(),
                    instance.getAttributeValues(),
                    instance.getClassLabel()
                ))
                .collect(Collectors.toList());
            response.setPreview(preview);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new DataUploadResponse(false, "Erreur lors du traitement du fichier: " + e.getMessage());
        }
    }

    private DatasetInitializer processArffFile(String fileName) {
        DatasetInitializer datasetInitializer = new DatasetInitializer();
        
        CustomFileReader fileReader = new CustomFileReader(fileName);
        List<Attribute> attributes = fileReader.getAttributs();
        
        // Créer les classes de décision à partir du dernier attribut (classe cible)
        List<DecisionTreeClass> decisionTreeClasses = new ArrayList<>();
        Attribute classAttribute = attributes.get(attributes.size() - 1);
        int i = 1;
        for (Branch branch : classAttribute.getBranches()) {
            decisionTreeClasses.add(new DecisionTreeClass(i, branch.getValue(), 0));
            i++;
        }

        // Configurer le dataset initializer
        datasetInitializer.setDataSetSource(fileName);
        datasetInitializer.setAttributes(new ArrayList<>(attributes.subList(0, attributes.size() - 1))); // Tous sauf le dernier
        datasetInitializer.setClassName(classAttribute.getAttributeName());
        datasetInitializer.setInstanceData(fileReader.getDataSet());
        datasetInitializer.setDecisionTreeClasses(decisionTreeClasses);

        return datasetInitializer;
    }

    public DatasetInitializer getCurrentDataset() {
        return currentDataset;
    }

    public boolean hasDataset() {
        return currentDataset != null && currentDataset.getInstanceData() != null && !currentDataset.getInstanceData().isEmpty();
    }
}

