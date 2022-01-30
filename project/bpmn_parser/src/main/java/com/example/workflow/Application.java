package com.example.workflow;

import camundajar.impl.scala.collection.Seq;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class Application {

  public static void main(String... args) {

    // Read a model from a file
    File file = new File("/home/guillem/Documents/upc/TFG/project/bpmn_models/A20/A.2.0.bpmn");
    BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);

    // Find all elements of the type Task
    Collection<ModelElementInstance> taskInstances = getElementsOfType(modelInstance, Task.class);
    for (ModelElementInstance taskInstance : taskInstances) {
      System.out.println(taskInstance.getAttributeValue("name"));
    }
    System.out.println();

    // Find all elements of type Start Event
    Collection<ModelElementInstance> startInstances = getElementsOfType(modelInstance, StartEvent.class);
    for (ModelElementInstance startInstance : startInstances) {
      StartEvent start = (StartEvent) startInstance;
      System.out.println(start.getName());
      Collection<SequenceFlow> sequenceFlow = (Collection<SequenceFlow>) start.getOutgoing();
      for (SequenceFlow flow : sequenceFlow) {
        FlowNode source = flow.getTarget();
        System.out.println(source.getName());
      }
    }
    System.out.println();

    ModelElementType flowNode = modelInstance.getModel().getType(FlowNode.class);
    System.out.println(flowNode);
  }

  public static Collection<ModelElementInstance> getElementsOfType(BpmnModelInstance modelInstance, Class c) {
    ModelElementType taskType = modelInstance.getModel().getType(c);
    return modelInstance.getModelElementsByType(taskType);
  }

  // Find the following flow nodes of a task or a gateway
  public static Collection<FlowNode> getFlowingFlowNodes(FlowNode node) {
    Collection<FlowNode> followingFlowNodes = new ArrayList<>();
    for (SequenceFlow sequenceFlow : node.getOutgoing()) {
      followingFlowNodes.add(sequenceFlow.getTarget());
    }
    return followingFlowNodes;
  }

  public static Collection<FlowNode> getAllFlowNodes(FlowNode source) {
    Collection<FlowNode> allFlowNodes = new ArrayList<>();
    Collection<FlowNode> flowNodes = getFlowingFlowNodes(source);
    for (FlowNode flowNode : flowNodes) {
      System.out.println(flowNode.getName());
    }
    return allFlowNodes;
  }

}