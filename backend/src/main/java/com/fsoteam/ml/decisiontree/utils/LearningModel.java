package com.fsoteam.ml.decisiontree.utils;

import com.fsoteam.ml.decisiontree.decisionTree.Attribute;
import com.fsoteam.ml.decisiontree.decisionTree.Node;
import com.fsoteam.ml.decisiontree.model.Instance;

import java.util.List;

public interface LearningModel extends Cloneable {

    public void train(List<Instance> instanceList);
    public int[][] generateConfusionMatrix(List<Instance> dataTest);
    public String evaluate(Instance instance);
    public LearningModel cloneModel();
}
