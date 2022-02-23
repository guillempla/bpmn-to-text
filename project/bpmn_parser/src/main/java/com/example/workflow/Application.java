package com.example.workflow;

import java.io.File;
import java.util.ArrayList;

public class Application {

  public static void main(String[] args) {
    String models_path = "../bpmn_models/";
    if (args.length > 0) {
      models_path = args[0];
    }

    ArrayList<String> bpmn_paths = getBpmnPaths(models_path);
    for (String path: bpmn_paths) {
      String bpmn_name = getBpmnNameFromPath(path);
      System.out.println(bpmn_name);
      ModelReader model = new ModelReader(path);
      ModelJSON modelJSON = new ModelJSON(bpmn_name, model.getElements(), model.getLanes());
      modelJSON.createElementsJSON();
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
    String[] paths_elements = path.split("/");
    return paths_elements[paths_elements.length-1].replace(".bpmn", "");
  }

}