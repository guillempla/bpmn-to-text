package com.example.workflow;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

import java.io.File;
import java.util.*;

public class ModelReader {
    private final BpmnModelInstance modelInstance;
    private final ArrayList<BPMNElements> bpmnElements;

    public ModelReader(String path) {
        File file = new File(path);
        this.modelInstance = Bpmn.readModelFromFile(file);
        this.bpmnElements = new ArrayList<>();
        this.saveElementsFromModel();
    }

    public ArrayList<BPMNElements> getBpmnElements() {
        return bpmnElements;
    }

    private void saveElementsFromModel() {
        // Find all elements of type Start Event
        Collection<ModelElementInstance> startInstances = getElementsOfType(StartEvent.class);

        // For each startEvent, save it and save its following elements
        for (ModelElementInstance startInstance : startInstances) {
            BPMNElements bpmnElement = new BPMNElements();

            FlowNode start = (FlowNode) startInstance;
            if (addElement(startInstance, bpmnElement)) {
                saveFollowingElements(start, bpmnElement);
            }
            bpmnElements.add(bpmnElement);
        }
    }

    private void saveFollowingElements(FlowNode node, BPMNElements bpmnElement) {
        ArrayList<Pair<String, String>> nextIDs = new ArrayList<>();

        // For each following node, save it and save its following elements
        for (SequenceFlow sequenceFlow : node.getOutgoing()) {
            FlowNode next = sequenceFlow.getTarget();
            Pair<String, String> pair = new ImmutablePair<>(next.getAttributeValue("id"), sequenceFlow.getName());
            nextIDs.add(pair);

            // If element has been saved, then save its following elements
            if (addElement(next, bpmnElement)) {
                saveFollowingElements(next, bpmnElement);
            }
        }

        String nodeID = node.getAttributeValue("id");
        bpmnElement.addNextElements(nodeID, nextIDs);
    }

    private boolean addElement(ModelElementInstance instance, BPMNElements bpmnElement) {
        String instanceId = instance.getAttributeValue("id");

        // Checks if instance have been previously saved, if not it saves the instance in elements
        if (!bpmnElement.elementsContainId(instanceId)) {
            bpmnElement.addElements(instanceId, instance);
            return true;
        }
        return false;
    }

    private Collection<ModelElementInstance> getElementsOfType(Class<? extends ModelElementInstance> c) {
        ModelElementType taskType = modelInstance.getModel().getType(c);
        return modelInstance.getModelElementsByType(taskType);
    }
}
