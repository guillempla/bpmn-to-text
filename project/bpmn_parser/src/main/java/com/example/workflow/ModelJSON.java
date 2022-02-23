package com.example.workflow;

import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class ModelJSON {
    public static final String PARSED_BPMN_PATH = "../parsed_bpmn/";

    String file_name;
    String json_path;
    Map<String, ModelElementInstance> elements;
    Map<String, ArrayList<ModelElementInstance>> lanes;
    ArrayList<String> attributes = new ArrayList<>(Arrays.asList("id", "name", "lane"));

    public ModelJSON(String file_name, Map<String, ModelElementInstance> elements, Map<String, ArrayList<ModelElementInstance>> lanes) {
        this.file_name = file_name;
        this.json_path = PARSED_BPMN_PATH + file_name + ".json";
        this.elements = elements;
        this.lanes = lanes;
    }

    public ModelJSON(String file_name, Map<String, ModelElementInstance> elements, Map<String, ArrayList<ModelElementInstance>> lanes, ArrayList<String> attributes) {
        this.file_name = file_name;
        this.json_path = PARSED_BPMN_PATH + file_name + ".json";
        this.elements = elements;
        this.lanes = lanes;
        this.attributes = attributes;
    }


    public void createElementsJSON() {
        JSONObject elements_json = new JSONObject();
        for (Map.Entry<String, ModelElementInstance> entry : elements.entrySet()) {
            JSONObject attributes_json = new JSONObject();
            for (String attribute : attributes) {
                String value = entry.getValue().getAttributeValue(attribute);
                if (value != null) {
                    value = value.replaceAll("([\\r\\n])", "");
                }
                attributes_json.put(attribute, value);
            }
            elements_json.put(entry.getKey().replace("\\", ""), attributes_json);
        }

        JSONObject wrapper = new JSONObject();
        wrapper.put(file_name, elements_json);

        saveJSON(wrapper);
    }

    private void saveJSON(JSONObject jsonObject) {
        try {
            FileWriter file = new FileWriter(json_path);
            file.write(jsonObject.toJSONString());
            file.close();
            System.out.println("JSON file created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
