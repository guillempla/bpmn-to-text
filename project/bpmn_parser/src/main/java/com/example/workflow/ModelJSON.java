package com.example.workflow;

import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class ModelJSON {
    public static final String PARSED_BPMN_PATH = "../bpmn_parsed/";

    String fileName;
    String jsonPath;
    Map<String, ModelElementInstance> elements;
    Map<String, ArrayList<ModelElementInstance>> lanes;
    ArrayList<String> attributes = new ArrayList<>(Arrays.asList("id", "name", "lane"));

    public ModelJSON(String fileName, Map<String, ModelElementInstance> elements, Map<String, ArrayList<ModelElementInstance>> lanes) {
        this.fileName = fileName;
        this.jsonPath = PARSED_BPMN_PATH + fileName + ".json";
        this.elements = elements;
        this.lanes = lanes;
    }

    public ModelJSON(String fileName, Map<String, ModelElementInstance> elements, Map<String, ArrayList<ModelElementInstance>> lanes, ArrayList<String> attributes) {
        this.fileName = fileName;
        this.jsonPath = PARSED_BPMN_PATH + fileName + ".json";
        this.elements = elements;
        this.lanes = lanes;
        this.attributes = attributes;
    }


    public void createElementsJSON() {
        JSONObject elementsJson = new JSONObject();
        for (Map.Entry<String, ModelElementInstance> entry : elements.entrySet()) {
            JSONObject attributesJson = new JSONObject();
            for (String attribute : attributes) {
                String value = entry.getValue().getAttributeValue(attribute);
                if (value != null) {
                    value = value.replaceAll("([\\r\\n])", " ");
                }
                attributesJson.put(attribute, value);
            }
            elementsJson.put(entry.getKey().replace("\\", ""), attributesJson);
        }

        JSONObject wrapper = new JSONObject();
        wrapper.put(fileName, elementsJson);

        saveJSON(wrapper);
    }

    private void saveJSON(JSONObject jsonObject) {
        try {
            FileWriter file = new FileWriter(jsonPath);
            file.write(jsonObject.toJSONString());
            file.close();
            System.out.println("JSON file created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
