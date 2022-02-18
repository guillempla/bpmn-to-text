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
    Map<String, ArrayList<ModelElementInstance>> lanes;

    public ModelReader(String path) {
        this.path = path;
        this.file = new File(path);
        this.modelInstance = Bpmn.readModelFromFile(file);
        this.elements = new HashMap<>();
        this.lanes = new HashMap<>();
        this.saveLanesFromModel();
        this.saveElementsFromModel();
    }

    public Map<String, ModelElementInstance> getElements() {
        return this.elements;
    }

    public Map<String, ArrayList<ModelElementInstance>> getLanes() {
        return this.lanes;
    }

    private void saveLanesFromModel() {
        Collection<ModelElementInstance> lanesSets = getElementsOfType(LaneSet.class);
        for (ModelElementInstance laneSetInstance : lanesSets) {
            LaneSet laneSet = (LaneSet) laneSetInstance;
            for (Lane lane : laneSet.getLanes()) {
                String lane_name = lane.getName();
                ArrayList<ModelElementInstance> elements = new ArrayList<>();
                for (FlowNode flowNodeRef : lane.getFlowNodeRefs()) {
                    String element_id = flowNodeRef.getId();
                    ModelElementInstance element = modelInstance.getModelElementById(element_id);
                    element.setAttributeValue("lane", lane_name);
                    elements.add(element);
                }
                lanes.put(lane_name, elements);
            }
        }
    }

    private void saveElementsFromModel() {
        // Find all elements of type Start Event
        Collection<ModelElementInstance> startInstances = getElementsOfType(StartEvent.class);
        for (ModelElementInstance startInstance : startInstances) {
            FlowNode start = (FlowNode) startInstance;
            if (addElement(startInstance)) {
                saveFollowingElements(start);
            }
        }
//        System.out.println();
    }

    private void saveFollowingElements(FlowNode node) {
        for (SequenceFlow sequenceFlow : node.getOutgoing()) {
            FlowNode next = sequenceFlow.getTarget();
            if (addElement(next)) {
                saveFollowingElements(next);
            }
        }
    }

    private boolean addElement(ModelElementInstance instance) {
        String instance_id = instance.getAttributeValue("id");
        if (!elements.containsKey(instance_id)) {
            elements.put(instance_id, instance);
//            System.out.println(instance_id + " " + instance.getAttributeValue("name"));
            return true;
        }
        return false;
    }

    private Collection<ModelElementInstance> getElementsOfType(Class<? extends ModelElementInstance> c) {
        ModelElementType taskType = modelInstance.getModel().getType(c);
        return modelInstance.getModelElementsByType(taskType);
    }
}
