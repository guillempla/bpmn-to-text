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
    ArrayList<String> attributes = new ArrayList<>(Arrays.asList("id", "type", "name", "lane"));

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
            ModelElementInstance value = entry.getValue();
            JSONObject attributesJson = getValueAttributes(value);
            elementsJson.put(entry.getKey().replace("\\", ""), attributesJson);
        }

        JSONObject wrapper = new JSONObject();
        wrapper.put(fileName, elementsJson);

        saveJSON(wrapper);
    }

    private JSONObject getValueAttributes(ModelElementInstance value) {
        JSONObject attributesJson = new JSONObject();

        // Get attributes from attributes list
        for (String attributeName : this.attributes) {
            String attributeValue;
            if (attributeName.equals("type")) {
                attributeValue = value.getElementType().getTypeName();
            }
            else {
                attributeValue = value.getAttributeValue(attributeName);
            }

            // If attributeValue exists, remove line breaks
            if (attributeValue != null) {
                attributeValue = attributeValue.replaceAll("([\\r\\n])", " ");
            }
            attributesJson.put(attributeName, attributeValue);
        }

        return attributesJson;
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
