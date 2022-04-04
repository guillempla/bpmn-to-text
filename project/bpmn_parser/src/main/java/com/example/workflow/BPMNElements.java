package com.example.workflow;

import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BPMNElements {
    private final Map<String, ModelElementInstance> elements;
    private final Map<String, ArrayList<Pair<String, String>>> nextElements;

    public BPMNElements() {
        this.elements = new HashMap<>();
        this.nextElements = new HashMap<>();
    }

    public Map<String, ModelElementInstance> getElements() {
        return elements;
    }

    public Map<String, ArrayList<Pair<String, String>>> getNextElements() {
        return nextElements;
    }

    public void addElements(String instanceId, ModelElementInstance instance) {
        elements.put(instanceId, instance);
    }

    public void addNextElements(String nodeID, ArrayList<Pair<String, String>> nextIDs) {
        nextElements.put(nodeID, nextIDs);
    }

    public boolean elementsContainId(String instanceId) {
        return elements.containsKey(instanceId);
    }
}
