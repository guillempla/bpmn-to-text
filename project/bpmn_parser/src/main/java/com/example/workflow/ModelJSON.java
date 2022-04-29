package com.example.workflow;

import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class ModelJSON {
    public static final String PARSED_BPMN_PATH = "../bpmn_parsed/";

    private final String fileName;
    private final String jsonPath;
    private final Map<String, ModelElementInstance> elements;
    private final Map<String, ArrayList<Pair<String, String>>> nextElements;
    private final Map<String, String> lanes;
    private final ArrayList<String> attributes = new ArrayList<>(Arrays.asList("id", "type", "name", "lane", "next"));

    public ModelJSON(String fileName, BPMNElements bpmnElements) {
        this.fileName = fileName;
        this.jsonPath = PARSED_BPMN_PATH + fileName + ".json";
        this.elements = bpmnElements.getElements();
        this.nextElements = bpmnElements.getNextElements();
        this.lanes = bpmnElements.getLanes();
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

            switch (attributeName) {
                case "next":
                    String valueID = value.getAttributeValue("id");
                    JSONArray nextValues = new JSONArray();
                    ArrayList<Pair<String, String>> pairs = nextElements.get(valueID);
                    for (Pair<String, String> pair : pairs) {
                        JSONObject nextValue = new JSONObject();
                        nextValue.put("id", pair.getKey());
                        nextValue.put("name", pair.getValue());
                        nextValues.add(nextValue);
                    }
                    attributeValue = nextValues;
                    break;
                case "type":
                    attributeValue = value.getElementType().getTypeName();
                    break;
                case "lane":
                    attributeValue = lanes.get(value.getAttributeValue("id"));
                    break;
                default:
                    attributeValue = value.getAttributeValue(attributeName);
                    break;
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
