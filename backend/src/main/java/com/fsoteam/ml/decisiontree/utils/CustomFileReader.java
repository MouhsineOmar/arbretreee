package com.fsoteam.ml.decisiontree.utils;

import com.fsoteam.ml.decisiontree.decisionTree.Attribute;
import com.fsoteam.ml.decisiontree.decisionTree.Branch;
import com.fsoteam.ml.decisiontree.model.Instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileReader;
import java.util.List;

public class CustomFileReader {

    private List<Instance> dataSet = new ArrayList<>();
    private List<Attribute> attributs = new ArrayList<>();

    public CustomFileReader(String fileName) {
        try {
            loadDataSetFromFile(fileName);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void loadDataSetFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().startsWith("@attribute")) {
                    int firstBraceIndex = line.indexOf("{");
                    int lastBraceIndex = line.indexOf("}");
                    String attributeName = line.substring(11, firstBraceIndex).trim();
                    String[] branches = line.substring(firstBraceIndex + 1, lastBraceIndex).replace(" ", "").split(",");
                    List<Branch> branchesList = new ArrayList<>();
                    for (String branch : branches) {
                        branchesList.add(new Branch(branch));
                    }
                    attributs.add(new Attribute(attributeName, branchesList));
                }
                if (line.toLowerCase().startsWith("@data")) {
                    String dataLine;
                    while ((dataLine = reader.readLine()) != null && !dataLine.startsWith("%")) {
                        List<String> attributeValues = new ArrayList<>(Arrays.asList(dataLine.split(",")));
                        String instanceClass = attributeValues.remove(attributeValues.size() - 1);
                        dataSet.add(new Instance(dataSet.size() + 1, attributeValues, instanceClass));
                    }
                    System.out.println("\nDataset loaded successfully\n");
                    break;
                }
            }
        }
    }

    public List<Instance> getDataSet() {
        return dataSet;
    }

    public List<Attribute> getAttributs() {
        return attributs;
    }
}