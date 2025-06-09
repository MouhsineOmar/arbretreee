package com.fsoteam.ml.decisiontreeimpl.ui;

import com.fsoteam.ml.decisiontreeimpl.decisionTree.*;
import com.fsoteam.ml.decisiontreeimpl.evaluation.EvaluationMetrics;
import com.fsoteam.ml.decisiontreeimpl.model.ConfusionMatrix;
import com.fsoteam.ml.decisiontreeimpl.model.Instance;
import com.fsoteam.ml.decisiontreeimpl.model.MemoryReboot;
import com.fsoteam.ml.decisiontreeimpl.model.TrainTest;
import com.fsoteam.ml.decisiontreeimpl.utils.ClassifierOutputHelper;
import com.fsoteam.ml.decisiontreeimpl.utils.DatasetInitializer;
import com.fsoteam.ml.decisiontreeimpl.utils.DetailedAccuracy;
import com.fsoteam.ml.decisiontreeimpl.utils.LearningModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

public class RunTestController {

    @FXML private Button startTestButton;
    @FXML private Button stopTestButton;
    @FXML private RadioButton classificationModel_Id3, classificationModel_CART, classificationModel_RandomForest;
    @FXML private TextField numberOfTrees;
    @FXML private ListView<String> testHistoryListView;
    @FXML private TextArea fileContentArea;

    private SharedData sharedData;
    private List<Instance> predictedInstances;
    private Map<String, MemoryReboot> history;

    @FXML
    public void initialize() {
        this.sharedData = SharedData.getInstance();
        this.predictedInstances = new ArrayList<>();
        this.history = new HashMap<>();

        startTestButton.setOnAction(event -> handleStartButtonClick());
        testHistoryListView.setOnMouseClicked(event -> handleHistoryReboot());
    }

    private void handleHistoryReboot() {
        String selectedMemory = testHistoryListView.getSelectionModel().getSelectedItem();
        MemoryReboot memory = history.get(selectedMemory);
        sharedData.setTrainingData(memory.getTrainingData());
        sharedData.setTestingData(memory.getTestingData());
        sharedData.setTrainedModel(memory.getTrainedModel().cloneModel());
        fileContentArea.setFont(Font.font("Monospaced", FontWeight.NORMAL, 12));
        fileContentArea.setText(memory.getMemoryOutput());
        fileContentArea.setWrapText(false);
        fileContentArea.setStyle("-fx-white-space: pre;");
    }

    private void handleStartButtonClick() {
        List<Instance> dataset = new ArrayList<>(sharedData.getDatasetInitializer().getInstanceData());

        dataset.removeIf(i -> {
            for (String val : i.getAttributeValues()) {
                boolean isValid = sharedData.getDatasetInitializer().getAttributes().stream()
                        .flatMap(attr -> attr.getBranches().stream())
                        .anyMatch(branch -> val.contains(branch.getValue()));
                if (!isValid) {
                    System.out.println("❌ Instance invalide : " + val);
                    return true;
                }
            }
            return false;
        });

        sharedData.getDatasetInitializer().setInstanceData(dataset);
        sharedData.setTrainingData(dataset);
        sharedData.setTestingData(dataset);

        long trainStart = System.currentTimeMillis();

        if (classificationModel_Id3.isSelected()) {
            sharedData.setLearningAlgorithm("ID3");
            sharedData.setTrainedModel(trainModelById3(dataset));
        } else if (classificationModel_CART.isSelected()) {
            sharedData.setLearningAlgorithm("CART");
            sharedData.setTrainedModel(trainModelByCART(dataset));
        } else if (classificationModel_RandomForest.isSelected()) {
            sharedData.setLearningAlgorithm("RandomForest");
            sharedData.setTrainedModel(trainModelByRandomForest(dataset));
        }

        long trainEnd = System.currentTimeMillis();

        long testStart = System.currentTimeMillis();
        testModel(sharedData.getTrainedModel(), dataset);
        long testEnd = System.currentTimeMillis();

        String output = generateClassifierOutput("Full dataset", trainEnd - trainStart, testEnd - testStart);

        String label = "Test-" + sharedData.getLearningAlgorithm() + "-" + LocalTime.now().withNano(0);
        MemoryReboot mem = new MemoryReboot();
        mem.setTrainingData(dataset);
        mem.setTestingData(dataset);
        mem.setTrainedModel(sharedData.getTrainedModel().cloneModel());
        mem.setMemoryOutput(output);
        mem.setMemoryName(label);

        testHistoryListView.getItems().add(label);
        history.put(label, mem);
        testHistoryListView.getSelectionModel().select(label);

        fileContentArea.setText(output);

        try {
            HelloController.modelHasTrained();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement des vues post-test.");
        }
    }

    private DecisionTree trainModelById3(List<Instance> trainData) {
        DecisionTree model = new Id3DecisionTreeImpl(new Node(), sharedData.getDatasetInitializer().getDecisionTreeClasses(), sharedData.getDatasetInitializer().getAttributes());
        model.train(trainData);
        return model;
    }

    private DecisionTree trainModelByCART(List<Instance> trainData) {
        DecisionTree model = new CartDecisionTreeImpl(new Node(), sharedData.getDatasetInitializer().getDecisionTreeClasses(), sharedData.getDatasetInitializer().getAttributes());
        model.train(trainData);
        return model;
    }

    private RandomForest trainModelByRandomForest(List<Instance> trainData) {
        int trees = 100;
        if (!numberOfTrees.getText().isEmpty()) {
            trees = Integer.parseInt(numberOfTrees.getText());
        }
        RandomForest model = new RandomForest(trees, sharedData.getDatasetInitializer().getAttributes(), sharedData.getDatasetInitializer().getDecisionTreeClasses());
        model.train(trainData);
        return model;
    }

    private void testModel(LearningModel model, List<Instance> testData) {
        predictedInstances.clear();
        for (Instance instance : testData) {
            String predictedClass = model.evaluate(instance);
            predictedInstances.add(new Instance(instance.getInstanceId(), instance.getAttributeValues(), predictedClass));
        }
    }

    public String generateClassifierOutput(String testMode, long trainDuration, long testDuration) {
        LearningModel trainedModel = sharedData.getTrainedModel();
        List<Instance> trainingData = sharedData.getTrainingData();
        DatasetInitializer datasetInitializer = sharedData.getDatasetInitializer();

        ConfusionMatrix confusionMatrix = new ConfusionMatrix(trainedModel.generateConfusionMatrix(trainingData), datasetInitializer.getDecisionTreeClasses().size());
        EvaluationMetrics evaluationMetrics = new EvaluationMetrics(confusionMatrix);

        double accuracy = evaluationMetrics.calculateAccuracy();
        double errorRate = evaluationMetrics.calculateErrorRate();

        List<DetailedAccuracy> detailedAccuracyList = new ArrayList<>();
        for (int i = 0; i < datasetInitializer.getDecisionTreeClasses().size(); i++) {
            DetailedAccuracy tmp = new DetailedAccuracy();
            tmp.setClassName(datasetInitializer.getDecisionTreeClasses().get(i).getClassName());
            tmp.setTpRate(evaluationMetrics.calculateTruePositiveRate(i));
            tmp.setFpRate(evaluationMetrics.calculateFalsePositiveRate(i));
            tmp.setPrecision(evaluationMetrics.calculateClassPrecision(i));
            tmp.setRecall(evaluationMetrics.calculateClassRecall(i));
            tmp.setfMeasure(evaluationMetrics.calculateClassFMeasure(i));
            tmp.setMcc(evaluationMetrics.calculateMatthewsCorrelationCoefficient(i));
            tmp.setRocArea(evaluationMetrics.calculateRocArea(i));
            tmp.setPrcArea(evaluationMetrics.calculatePrcArea(i));
            detailedAccuracyList.add(tmp);
        }

        ClassifierOutputHelper outputHelper = new ClassifierOutputHelper();
        outputHelper.setScheme(trainedModel.getClass().getName());
        outputHelper.setRelation(datasetInitializer.getDataSetSource());
        outputHelper.setDatasetSize(trainingData.size());
        outputHelper.setAttributes(datasetInitializer.getAttributes());
        outputHelper.setTestMode(testMode);
        outputHelper.setTestAlgorithm(trainedModel.getClass().getName() + "(" + sharedData.getLearningAlgorithm() + ")");
        outputHelper.setBuildTime(String.valueOf(trainDuration / 1000.0));
        outputHelper.setTestTime(String.valueOf(testDuration / 1000.0));
        outputHelper.setCorrect((int) (accuracy * trainingData.size()));
        outputHelper.setCorrectPercentage(accuracy * 100);
        outputHelper.setIncorrect((int) (errorRate * trainingData.size()));
        outputHelper.setIncorrectPercentage(errorRate * 100);
        outputHelper.setKappaStatistic(evaluationMetrics.calculateKappa());

        outputHelper.setMeanAbsoluteError(0);
        outputHelper.setRootMeanSquaredError(0);
        outputHelper.setRelativeAbsoluteError(0);
        outputHelper.setRootRelativeSquaredError(0);

        outputHelper.setTotalNumberOfInstances(trainingData.size());
        outputHelper.setDetailedAccuracyByClass(detailedAccuracyList);
        outputHelper.setConfusionMatrixValues(confusionMatrix.getMatrix());

        String output = outputHelper.generateOutput();
        output = output.replaceAll("^\\s+", "\u00A0").replaceAll("\\s+$", "\u00A0");
        fileContentArea.setFont(Font.font("Monospaced", FontWeight.NORMAL, 12));
        fileContentArea.setText(output);
        fileContentArea.setWrapText(false);
        fileContentArea.setStyle("-fx-white-space: pre;");
        return output;
    }
}
