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

    String fileName;
    String path;

    JSONObject jsonElements;
    Map<String, ElementVertex> vertexElements;
    MultiDirectedGraph graph;

    public JSONReader(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
        this.jsonElements = parseJSON(this.path);
        this.vertexElements = this.createVertexes();
        this.graph = this.buildGraph();
        ParagraphGenerator joiner = new ParagraphGenerator(this.graph);
        System.out.println(joiner.getJoinedSentences());
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
        return (ArrayList<String>) jsonElement.get("next");
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
        JSONObject finalPhraseJSON = (JSONObject) jsonElement.get("finalPhrase");
        NLGElement phrase = retrievePhrase(originalSentence, finalPhraseJSON);

        return new ElementVertex(id, sentence, phrase, type);
    }

    private NLGElement retrievePhrase(String originalSentence, JSONObject jsonElement) {
        PhraseRetriever retriever = new PhraseRetriever(originalSentence, jsonElement);
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

