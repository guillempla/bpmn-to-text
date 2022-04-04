package com.example.workflow;

import org.apache.commons.lang3.tuple.Pair;
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
    Map<String, ArrayList<Pair<String, String>>> nextElements;
    ArrayList<String> attributes = new ArrayList<>(Arrays.asList("id", "type", "name", "lane", "next"));

    public ModelJSON(String fileName, Map<String, ModelElementInstance> elements, Map<String,
            ArrayList<Pair<String, String>>> nextElements) {
        this.fileName = fileName;
        this.jsonPath = PARSED_BPMN_PATH + fileName + ".json";
        this.elements = elements;
        this.nextElements = nextElements;
    }

    public void createElementsJSON() {
        JSONObject elementsJson = new JSONObject();

        // For every entry on elements Map, get its attributes and save them into a JSONObject
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
        for (String attributeName : attributes) {
            Object attributeValue;

            if (attributeName.equals("next")) {
                String valueID = value.getAttributeValue("id");
                JSONObject nextValue = new JSONObject();
                ArrayList<Pair<String, String>> pairs = nextElements.get(valueID);
                for (Pair<String, String> pair : pairs) {
                    nextValue.put("id", pair.getKey());
                    nextValue.put("name", pair.getValue());
                }
                attributeValue = nextValue;
            }
            else if (attributeName.equals("type")) {
                attributeValue = value.getElementType().getTypeName();
            }
            else {
                attributeValue = value.getAttributeValue(attributeName);
            }

            // If attributeValue exists and is not an array, remove line breaks
            if (attributeValue != null && !attributeName.equals("next")) {
                String attributeString = (String) attributeValue;
                attributeValue = attributeString.replaceAll("([\\r\\n])", " ");
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
