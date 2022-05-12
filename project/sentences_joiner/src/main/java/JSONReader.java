import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import simplenlg.framework.NLGElement;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSONReader {
    public static final String GENERATED_PATH = "../sentences_joined/";

    private final String fileName;

    private final JSONObject jsonElements;
    private final Map<String, ElementVertex> vertexElements;

    public JSONReader(String fileName, String path) {
        this.fileName = fileName;
        this.jsonElements = parseJSON(path);
        this.vertexElements = this.createVertexes();
        MultiDirectedGraph graph = this.buildGraph();
        System.out.println(fileName);
        ParagraphGenerator joiner = new ParagraphGenerator(graph);
        System.out.println(joiner.getParagraph());
    }

    private MultiDirectedGraph buildGraph() {
        MultiDirectedGraph graph = new MultiDirectedGraph();

        for (Object key : jsonElements.keySet()) {
            String elementId = key.toString();
            JSONObject jsonElement = (JSONObject) jsonElements.get(key);
            ArrayList<String> nextIds = getNextIds(jsonElement);
            for (String nextId : nextIds) {
                Vertex sourceVertex = vertexElements.get(elementId);
                Vertex nextVertex = vertexElements.get(nextId);
                graph.addEdge(sourceVertex, nextVertex);
            }
        }

        return graph;
    }

    private ArrayList<String> getNextIds(JSONObject jsonElement) {
        ArrayList<String> nextIds = new ArrayList<>();
        ArrayList<Pair<String, String>> pairs = retrieveNext(jsonElement);
        for (Pair<String, String> pair : pairs) {
            nextIds.add(pair.getKey());
        }
        return nextIds;
    }

    private Map<String, ElementVertex> createVertexes() {
        Map<String, ElementVertex> elements = new HashMap<>();

        for (Object key : jsonElements.keySet()) {
            JSONObject jsonElement = (JSONObject) jsonElements.get(key);

            ElementVertex vertex = createVertex(jsonElement);
            elements.put(key.toString(), vertex);
        }

        return elements;
    }

    private ElementVertex createVertex(JSONObject jsonElement) {
        String id = JSONUtils.getStringFromJSON(jsonElement, "id");
        String sentence = JSONUtils.getStringFromJSON(jsonElement, "finalSentence");
        String type = JSONUtils.getStringFromJSON(jsonElement, "type");

        String originalSentence = JSONUtils.getStringFromJSON(jsonElement, "name");
        String finalSentence = JSONUtils.getStringFromJSON(jsonElement, "finalSentence");
        JSONObject finalPhraseJSON = (JSONObject) jsonElement.get("finalPhrase");
        NLGElement phrase = retrievePhrase(originalSentence, finalSentence, finalPhraseJSON);

        ArrayList<Pair<String, String>> next = retrieveNext(jsonElement);

        return new ElementVertex(id, sentence, phrase, type, next);
    }

    private ArrayList<Pair<String, String>> retrieveNext(JSONObject jsonElement) {
        ArrayList<Pair<String, String>> next = new ArrayList<>();
        ArrayList<JSONObject> nextJSON = (ArrayList<JSONObject>) jsonElement.get("next");
        for (JSONObject jsonObject : nextJSON) {
            String id = JSONUtils.getStringFromJSON(jsonObject, "id");
            String name = JSONUtils.getStringFromJSON(jsonObject, "name");
            Pair<String, String> pair = new ImmutablePair<>(id, name);
            next.add(pair);
        }
        return next;
    }

    private NLGElement retrievePhrase(String originalSentence, String finalSentence, JSONObject jsonElement) {
        PhraseRetriever retriever = new PhraseRetriever(originalSentence, finalSentence, jsonElement);
        return retriever.getPhrase();
    }

    private JSONObject parseJSON(String path) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(path));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void saveJSON() {
        try {
            FileWriter file = new FileWriter(GENERATED_PATH + fileName + ".json");
            file.write(jsonElements.toJSONString());
            file.close();
            System.out.println("JSON file with joined sentences created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

