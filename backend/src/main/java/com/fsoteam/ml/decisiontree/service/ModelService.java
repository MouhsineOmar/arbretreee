package com.fsoteam.ml.decisiontree.service;

import com.fsoteam.ml.decisiontree.decisionTree.*;
import com.fsoteam.ml.decisiontree.evaluation.EvaluationMetrics;
import com.fsoteam.ml.decisiontree.model.*;
import com.fsoteam.ml.decisiontree.utils.ClassifierOutputHelper;
import com.fsoteam.ml.decisiontree.utils.DatasetInitializer;
import com.fsoteam.ml.decisiontree.utils.DetailedAccuracy;
import com.fsoteam.ml.decisiontree.utils.LearningModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelService {

    @Autowired
    private DataService dataService;

    private LearningModel currentModel;
    private String currentAlgorithm;
    private List<Instance> trainingData;
    private List<Instance> testingData;

    public TrainingResponse trainModel(TrainingRequest request) {
        try {
            if (!dataService.hasDataset()) {
                return new TrainingResponse(false, "Aucun dataset n'a été chargé");
            }

            DatasetInitializer dataset = dataService.getCurrentDataset();
            List<Instance> allData = new ArrayList<>(dataset.getInstanceData());

            // Nettoyer les données (supprimer les instances invalides)
            allData.removeIf(instance -> {
                for (String val : instance.getAttributeValues()) {
                    boolean isValid = dataset.getAttributes().stream()
                            .flatMap(attr -> attr.getBranches().stream())
                            .anyMatch(branch -> val.contains(branch.getValue()));
                    if (!isValid) {
                        return true;
                    }
                }
                return false;
            });

            this.trainingData = allData;
            this.testingData = allData; // Pour l'instant, on utilise le même dataset pour test et entraînement
            this.currentAlgorithm = request.getAlgorithm();

            // Entraînement
            long trainStart = System.currentTimeMillis();
            LearningModel model = createModel(request, dataset);
            model.train(trainingData);
            long trainEnd = System.currentTimeMillis();

            this.currentModel = model;

            // Test
            long testStart = System.currentTimeMillis();
            List<Instance> predictions = testModel(model, testingData);
            long testEnd = System.currentTimeMillis();

            // Calcul des métriques
            ConfusionMatrix confusionMatrix = new ConfusionMatrix(
                model.generateConfusionMatrix(trainingData), 
                dataset.getDecisionTreeClasses().size()
            );
            EvaluationMetrics metrics = new EvaluationMetrics(confusionMatrix);

            // Créer la réponse
            TrainingResponse response = new TrainingResponse(true, "Modèle entraîné avec succès");
            response.setAlgorithm(request.getAlgorithm());
            response.setTrainingTimeMs(trainEnd - trainStart);
            response.setTestingTimeMs(testEnd - testStart);
            response.setAccuracy(metrics.calculateAccuracy());
            response.setErrorRate(metrics.calculateErrorRate());

            // Matrice de confusion
            int[][] matrix = confusionMatrix.getMatrix();
            List<List<Integer>> matrixList = Arrays.stream(matrix)
                .map(row -> Arrays.stream(row).boxed().collect(Collectors.toList()))
                .collect(Collectors.toList());
            response.setConfusionMatrix(matrixList);

            // Métriques par classe
            List<TrainingResponse.ClassMetrics> classMetrics = new ArrayList<>();
            for (int i = 0; i < dataset.getDecisionTreeClasses().size(); i++) {
                DecisionTreeClass dtClass = dataset.getDecisionTreeClasses().get(i);
                classMetrics.add(new TrainingResponse.ClassMetrics(
                    dtClass.getClassName(),
                    metrics.calculateClassPrecision(i),
                    metrics.calculateClassRecall(i),
                    metrics.calculateClassFMeasure(i),
                    metrics.calculateTruePositiveRate(i),
                    metrics.calculateFalsePositiveRate(i)
                ));
            }
            response.setClassMetrics(classMetrics);

            // Sortie détaillée
            response.setDetailedOutput(generateDetailedOutput(dataset, metrics, trainEnd - trainStart, testEnd - testStart));

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new TrainingResponse(false, "Erreur lors de l'entraînement: " + e.getMessage());
        }
    }

    private LearningModel createModel(TrainingRequest request, DatasetInitializer dataset) {
        switch (request.getAlgorithm().toUpperCase()) {
            case "ID3":
                return new Id3DecisionTreeImpl(
                    new Node(), 
                    dataset.getDecisionTreeClasses(), 
                    dataset.getAttributes()
                );
            case "CART":
                return new CartDecisionTreeImpl(
                    new Node(), 
                    dataset.getDecisionTreeClasses(), 
                    dataset.getAttributes()
                );
            case "RANDOMFOREST":
                return new RandomForest(
                    request.getNumberOfTrees(), 
                    dataset.getAttributes(), 
                    dataset.getDecisionTreeClasses()
                );
            default:
                throw new IllegalArgumentException("Algorithme non supporté: " + request.getAlgorithm());
        }
    }

    private List<Instance> testModel(LearningModel model, List<Instance> testData) {
        List<Instance> predictions = new ArrayList<>();
        for (Instance instance : testData) {
            String predictedClass = model.evaluate(instance);
            predictions.add(new Instance(
                instance.getInstanceId(), 
                instance.getAttributeValues(), 
                predictedClass
            ));
        }
        return predictions;
    }

    private String generateDetailedOutput(DatasetInitializer dataset, EvaluationMetrics metrics, long trainTime, long testTime) {
        ClassifierOutputHelper outputHelper = new ClassifierOutputHelper();
        outputHelper.setScheme(currentModel.getClass().getName());
        outputHelper.setRelation(dataset.getDataSetSource());
        outputHelper.setDatasetSize(trainingData.size());
        outputHelper.setAttributes(dataset.getAttributes());
        outputHelper.setTestMode("Full dataset");
        outputHelper.setTestAlgorithm(currentModel.getClass().getName() + "(" + currentAlgorithm + ")");
        outputHelper.setBuildTime(String.valueOf(trainTime / 1000.0));
        outputHelper.setTestTime(String.valueOf(testTime / 1000.0));
        
        double accuracy = metrics.calculateAccuracy();
        double errorRate = metrics.calculateErrorRate();
        
        outputHelper.setCorrect((int) (accuracy * trainingData.size()));
        outputHelper.setCorrectPercentage(accuracy * 100);
        outputHelper.setIncorrect((int) (errorRate * trainingData.size()));
        outputHelper.setIncorrectPercentage(errorRate * 100);
        outputHelper.setKappaStatistic(metrics.calculateKappa());

        // Métriques détaillées par classe
        List<DetailedAccuracy> detailedAccuracyList = new ArrayList<>();
        for (int i = 0; i < dataset.getDecisionTreeClasses().size(); i++) {
            DetailedAccuracy tmp = new DetailedAccuracy();
            tmp.setClassName(dataset.getDecisionTreeClasses().get(i).getClassName());
            tmp.setTpRate(metrics.calculateTruePositiveRate(i));
            tmp.setFpRate(metrics.calculateFalsePositiveRate(i));
            tmp.setPrecision(metrics.calculateClassPrecision(i));
            tmp.setRecall(metrics.calculateClassRecall(i));
            tmp.setfMeasure(metrics.calculateClassFMeasure(i));
            tmp.setMcc(metrics.calculateMatthewsCorrelationCoefficient(i));
            tmp.setRocArea(metrics.calculateRocArea(i));
            tmp.setPrcArea(metrics.calculatePrcArea(i));
            detailedAccuracyList.add(tmp);
        }
        outputHelper.setDetailedAccuracyByClass(detailedAccuracyList);

        ConfusionMatrix confusionMatrix = new ConfusionMatrix(
            currentModel.generateConfusionMatrix(trainingData), 
            dataset.getDecisionTreeClasses().size()
        );
        outputHelper.setConfusionMatrixValues(confusionMatrix.getMatrix());

        return outputHelper.generateOutput();
    }

    public LearningModel getCurrentModel() {
        return currentModel;
    }

    public boolean hasTrainedModel() {
        return currentModel != null;
    }
}

