package com.example.workflow;

import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BPMNElements {
    private Map<String, ModelElementInstance> elements;
    private Map<String, ArrayList<String>> nextElements;

    public BPMNElements() {
        this.elements = new HashMap<>();
        this.nextElements = new HashMap<>();
    }

    public Map<String, ModelElementInstance> getElements() {
        return elements;
    }

    public Map<String, ArrayList<String>> getNextElements() {
        return nextElements;
    }

    public void addElements(String instanceId, ModelElementInstance instance) {
        elements.put(instanceId, instance);
    }

    public void addNextElements(String nodeID, ArrayList<String> nextIDs) {
        nextElements.put(nodeID, nextIDs);
    }

    public boolean elementsContainId(String instanceId) {
        return elements.containsKey(instanceId);
    }
}
