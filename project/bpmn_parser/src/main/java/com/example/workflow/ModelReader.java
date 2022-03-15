package com.example.workflow;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

import java.io.File;
import java.util.*;

public class ModelReader {
    String path;
    File file;
    BpmnModelInstance modelInstance;
    Map<String, ModelElementInstance> elements;
    Map<String, ArrayList<String>> nextElements;
    Map<String, ArrayList<ModelElementInstance>> lanes;

    public ModelReader(String path) {
        this.path = path;
        this.file = new File(path);
        this.modelInstance = Bpmn.readModelFromFile(file);
        this.elements = new HashMap<>();
        this.nextElements = new HashMap<>();
        this.lanes = new HashMap<>();
        this.saveLanesFromModel();
        this.saveElementsFromModel();
    }

    public Map<String, ModelElementInstance> getElements() {
        return this.elements;
    }

    public Map<String, ArrayList<String>> getNextElements() { return this.nextElements; }

    public Map<String, ArrayList<ModelElementInstance>> getLanes() {
        return this.lanes;
    }

    private void saveLanesFromModel() {
        Collection<ModelElementInstance> lanesSets = getElementsOfType(LaneSet.class);
        for (ModelElementInstance laneSetInstance : lanesSets) {
            LaneSet laneSet = (LaneSet) laneSetInstance;
            for (Lane lane : laneSet.getLanes()) {
                String laneName = lane.getName();
                ArrayList<ModelElementInstance> elements = new ArrayList<>();
                for (FlowNode flowNodeRef : lane.getFlowNodeRefs()) {
                    String elementId = flowNodeRef.getId();
                    ModelElementInstance element = modelInstance.getModelElementById(elementId);
                    element.setAttributeValue("lane", laneName);
                    elements.add(element);
                }
                lanes.put(laneName, elements);
            }
        }
    }

    private void saveElementsFromModel() {
        // Find all elements of type Start Event
        Collection<ModelElementInstance> startInstances = getElementsOfType(StartEvent.class);

        // For each startEvent, save it and save its following elements
        for (ModelElementInstance startInstance : startInstances) {
            FlowNode start = (FlowNode) startInstance;
            if (addElement(startInstance)) {
                saveFollowingElements(start);
            }
        }
    }

    private void saveFollowingElements(FlowNode node) {
        ArrayList<String> nextIDs = new ArrayList<>();

        // For each following node, save it and save its following elements
        for (SequenceFlow sequenceFlow : node.getOutgoing()) {
            FlowNode next = sequenceFlow.getTarget();
            nextIDs.add(next.getAttributeValue("id"));

            // If element has been saved, then save its following elements
            if (addElement(next)) {
                saveFollowingElements(next);
            }
        }

        String nodeID = node.getAttributeValue("id");
        nextElements.put(nodeID, nextIDs);
    }

    private boolean addElement(ModelElementInstance instance) {
        String instanceId = instance.getAttributeValue("id");

        // Checks if instance have been previously saved, if not it saves the instance in elements
        if (!elements.containsKey(instanceId)) {
            elements.put(instanceId, instance);
//            System.out.println(instanceId + " " + instance.getAttributeValue("name"));
            return true;
        }
        return false;
    }

    private Collection<ModelElementInstance> getElementsOfType(Class<? extends ModelElementInstance> c) {
        ModelElementType taskType = modelInstance.getModel().getType(c);
        return modelInstance.getModelElementsByType(taskType);
    }
}
