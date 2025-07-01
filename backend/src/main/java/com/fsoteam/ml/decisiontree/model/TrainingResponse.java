package com.fsoteam.ml.decisiontree.model;

import java.util.List;

public class TrainingResponse {
    private boolean success;
    private String message;
    private String algorithm;
    private long trainingTimeMs;
    private long testingTimeMs;
    private double accuracy;
    private double errorRate;
    private String detailedOutput;
    private List<List<Integer>> confusionMatrix;
    private List<ClassMetrics> classMetrics;

    // Constructeurs
    public TrainingResponse() {}

    public TrainingResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters et Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public long getTrainingTimeMs() { return trainingTimeMs; }
    public void setTrainingTimeMs(long trainingTimeMs) { this.trainingTimeMs = trainingTimeMs; }

    public long getTestingTimeMs() { return testingTimeMs; }
    public void setTestingTimeMs(long testingTimeMs) { this.testingTimeMs = testingTimeMs; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public double getErrorRate() { return errorRate; }
    public void setErrorRate(double errorRate) { this.errorRate = errorRate; }

    public String getDetailedOutput() { return detailedOutput; }
    public void setDetailedOutput(String detailedOutput) { this.detailedOutput = detailedOutput; }

    public List<List<Integer>> getConfusionMatrix() { return confusionMatrix; }
    public void setConfusionMatrix(List<List<Integer>> confusionMatrix) { this.confusionMatrix = confusionMatrix; }

    public List<ClassMetrics> getClassMetrics() { return classMetrics; }
    public void setClassMetrics(List<ClassMetrics> classMetrics) { this.classMetrics = classMetrics; }

    public static class ClassMetrics {
        private String className;
        private double precision;
        private double recall;
        private double fMeasure;
        private double tpRate;
        private double fpRate;

        public ClassMetrics() {}

        public ClassMetrics(String className, double precision, double recall, double fMeasure, double tpRate, double fpRate) {
            this.className = className;
            this.precision = precision;
            this.recall = recall;
            this.fMeasure = fMeasure;
            this.tpRate = tpRate;
            this.fpRate = fpRate;
        }

        // Getters et Setters
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public double getPrecision() { return precision; }
        public void setPrecision(double precision) { this.precision = precision; }

        public double getRecall() { return recall; }
        public void setRecall(double recall) { this.recall = recall; }

        public double getfMeasure() { return fMeasure; }
        public void setfMeasure(double fMeasure) { this.fMeasure = fMeasure; }

        public double getTpRate() { return tpRate; }
        public void setTpRate(double tpRate) { this.tpRate = tpRate; }

        public double getFpRate() { return fpRate; }
        public void setFpRate(double fpRate) { this.fpRate = fpRate; }
    }
}

