package com.example.workflow;

import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BPMNElements {
    private final Map<String, ModelElementInstance> elements;
    private final Map<String, ArrayList<Pair<String, String>>> nextElements;
    private final Map<String, String> lanes;

    public BPMNElements() {
        this.elements = new HashMap<>();
        this.nextElements = new HashMap<>();
        this.lanes = new HashMap<>();
    }

    public Map<String, ModelElementInstance> getElements() {
        return elements;
    }

    public Map<String, ArrayList<Pair<String, String>>> getNextElements() {
        return nextElements;
    }

    public Map<String, String> getLanes() {
        return lanes;
    }

    public void addElements(String elementId, ModelElementInstance instance) {
        elements.put(elementId, instance);
    }

    public void addNextElements(String elementId, ArrayList<Pair<String, String>> nextIDs) {
        nextElements.put(elementId, nextIDs);
    }

    public void addLane(String elementId, String lane) {
        if (elements.containsKey(elementId)) {
            lanes.put(elementId, lane);
        }
    }

    public boolean elementsContainId(String instanceId) {
        return elements.containsKey(instanceId);
    }
}
