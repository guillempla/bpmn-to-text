package com.example.workflow;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
      System.out.println();
    }

//    createJSON("test");
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

  public static void createJSON(String file_name) {
    String path = "../parsed_bpmn/" + file_name + ".json";

    // Creating a JSONObject object
    JSONObject jsonObject = new JSONObject();

    // Insert key-value pairs into the json object
//    jsonObject.put("ID", "1");
//    jsonObject.put("First_Name", "Shikhar");
//    jsonObject.put("Last_Name", "Dhawan");
//    jsonObject.put("Date_Of_Birth", "1981-12-05");
//    jsonObject.put("Place_Of_Birth", "Delhi");
//    jsonObject.put("Country", "India");

    // Save JSON
    try {
      FileWriter file = new FileWriter(path);
      file.write(jsonObject.toJSONString());
      file.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("JSON file created: " + jsonObject);
  }

}