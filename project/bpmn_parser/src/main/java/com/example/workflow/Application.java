package com.example.workflow;

import java.io.File;
import java.util.ArrayList;

public class Application {

  public static void main(String[] args) {
    String modelsPath = "../bpmn_models/";
    if (args.length > 0) {
      modelsPath = args[0];
    }

    ArrayList<String> bpmnPaths = getBpmnPaths(modelsPath);
    for (String path: bpmnPaths) {
      String bpmnName = getBpmnNameFromPath(path);
      System.out.println(bpmnName);
      ModelReader model = new ModelReader(path);
      saveToJSON(bpmnName, model);
      System.out.println();
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

  public static String getBpmnNameFromPath(String path) {
    String[] pathsElements = path.split("/");
    return pathsElements[pathsElements.length-1].replace(".bpmn", "");
  }

  private static void saveToJSON(String bpmnName, ModelReader model) {
    ArrayList<BPMNElements> bpmnElements = model.getBpmnElements();
    int index = 1;
    for (BPMNElements bpmnElement : bpmnElements) {
      String name = bpmnName + "." + index++;
      ModelJSON modelJSON = new ModelJSON(name, bpmnElement.getElements(), bpmnElement.getNextElements());
      modelJSON.createElementsJSON();
    }
  }

}