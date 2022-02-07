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

  public static void main(String[] args) {
    String models_path = "../bpmn_models/";
    if (args.length > 0) {
      models_path = args[0];
    }

    ArrayList<String> bpmn_paths = getBpmnPaths(models_path);
    for (String path: bpmn_paths) {
      readModelFromFile(path);
    }
  }

  public static ArrayList<String> getBpmnPaths(String path) {
    ArrayList<String> paths = new ArrayList<>();

    File directoryPath = new File(path);
    //List of all files and directories
    File[] filesList = directoryPath.listFiles();
    if (filesList != null) {
      for (File file : filesList) {

        File filePath = new File(file.getAbsolutePath());
        //List of all files and directories
        String[] contents = filePath.list();
        if (contents != null) {
          for (String content : contents) {
            if (content.contains(".bpmn")) {
              paths.add(file.getAbsolutePath()+"/"+content);
            }
          }
        }
      }
    }
    return paths;
  }

  public static void readModelFromFile(String path) {
    // Read a model from a file
    File file = new File(path);
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
      Collection<SequenceFlow> sequenceFlow = start.getOutgoing();
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