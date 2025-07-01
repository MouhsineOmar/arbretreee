package com.fsoteam.ml.decisiontree.model;

import com.fsoteam.ml.decisiontree.decisionTree.DecisionTree;
import com.fsoteam.ml.decisiontree.utils.LearningModel;

import java.util.List;

public class MemoryReboot {
    private String memoryName;
    private List<Instance> trainingData;
    private List<Instance> testingData;
    private LearningModel trainedModel;
    private String memoryOutput;

    public MemoryReboot() {
        this.trainingData = null;
        this.testingData = null;
        this.trainedModel = null;
        this.memoryOutput = "";
        this.memoryName = "";
    }

    public MemoryReboot(List<Instance> trainingData, List<Instance> testingData, DecisionTree trainedModel) {
        this.trainingData = trainingData;
        this.testingData = testingData;
        this.trainedModel = trainedModel;
    }

    public List<Instance> getTrainingData() {
        return trainingData;
    }

    public void setTrainingData(List<Instance> trainingData) {
        this.trainingData = trainingData;
    }

    public List<Instance> getTestingData() {
        return testingData;
    }

    public void setTestingData(List<Instance> testingData) {
        this.testingData = testingData;
    }

    public LearningModel getTrainedModel() {
        return trainedModel;
    }

    public void setTrainedModel(LearningModel trainedModel) {
        this.trainedModel = trainedModel;
    }

    public String getMemoryName() {
        return memoryName;
    }

    public void setMemoryName(String memoryName) {
        this.memoryName = memoryName;
    }

    public String getMemoryOutput() {
        return memoryOutput;
    }

    public void setMemoryOutput(String memoryOutput) {
        this.memoryOutput = memoryOutput;
    }
}
