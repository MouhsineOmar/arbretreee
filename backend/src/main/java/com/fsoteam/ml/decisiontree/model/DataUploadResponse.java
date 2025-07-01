package com.fsoteam.ml.decisiontree.model;

import java.util.List;

public class DataUploadResponse {
    private boolean success;
    private String message;
    private List<String> attributeNames;
    private String className;
    private int instanceCount;
    private List<InstanceData> preview;

    // Constructeurs
    public DataUploadResponse() {}

    public DataUploadResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters et Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getAttributeNames() { return attributeNames; }
    public void setAttributeNames(List<String> attributeNames) { this.attributeNames = attributeNames; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public int getInstanceCount() { return instanceCount; }
    public void setInstanceCount(int instanceCount) { this.instanceCount = instanceCount; }

    public List<InstanceData> getPreview() { return preview; }
    public void setPreview(List<InstanceData> preview) { this.preview = preview; }

    public static class InstanceData {
        private int id;
        private List<String> values;
        private String classLabel;

        public InstanceData() {}

        public InstanceData(int id, List<String> values, String classLabel) {
            this.id = id;
            this.values = values;
            this.classLabel = classLabel;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public List<String> getValues() { return values; }
        public void setValues(List<String> values) { this.values = values; }

        public String getClassLabel() { return classLabel; }
        public void setClassLabel(String classLabel) { this.classLabel = classLabel; }
    }
}

