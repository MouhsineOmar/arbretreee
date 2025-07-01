package com.fsoteam.ml.decisiontree.model;

public class TrainingRequest {
    private String algorithm; // "ID3", "CART", "RandomForest"
    private int numberOfTrees = 100; // Pour Random Forest
    private double trainTestSplit = 1.0; // 1.0 = utiliser tout le dataset pour l'entraînement

    // Constructeurs
    public TrainingRequest() {}

    public TrainingRequest(String algorithm) {
        this.algorithm = algorithm;
    }

    // Getters et Setters
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public int getNumberOfTrees() { return numberOfTrees; }
    public void setNumberOfTrees(int numberOfTrees) { this.numberOfTrees = numberOfTrees; }

    public double getTrainTestSplit() { return trainTestSplit; }
    public void setTrainTestSplit(double trainTestSplit) { this.trainTestSplit = trainTestSplit; }
}

