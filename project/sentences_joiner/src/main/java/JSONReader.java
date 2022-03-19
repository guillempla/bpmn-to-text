import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import simplenlg.phrasespec.SPhraseSpec;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class JSONReader {
    public static final String GENERATED_PATH = "../sentences_joined/";

    String fileName;
    String path;

    JSONObject jsonElements;
    ArrayList<ElementVertex> vertexElements;

    public JSONReader(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
        this.jsonElements = parseJSON(this.path);
//        testJBPT();
        this.vertexElements = this.retrieveElements();
    }

    private ArrayList<ElementVertex> retrieveElements() {
        ArrayList<ElementVertex> elements = new ArrayList<>();

        for (Object key : jsonElements.keySet()) {
            JSONObject jsonElement = (JSONObject) jsonElements.get(key);
            String id = getStringFromJSON(jsonElement, "id");
            String sentence = getStringFromJSON(jsonElement, "finalSentence");
            SPhraseSpec phrase = retrievePhrase((JSONObject) jsonElement.get("finalPhrase"));
            String type = getStringFromJSON(jsonElement, "type");
            ElementVertex vertex = new ElementVertex(id, sentence, phrase, type);

            elements.add(vertex);
        }

        return elements;
    }

    private SPhraseSpec retrievePhrase(JSONObject jsonElement) {
        Boolean objectDeterminant = getBooleanFromJSON(jsonElement, "objectDeterminant");
        Boolean interrogative = getBooleanFromJSON(jsonElement, "interrogative");
        Boolean objectPlural = getBooleanFromJSON(jsonElement, "objectPlural");
        Boolean existActions = getBooleanFromJSON(jsonElement, "existsActions");
        String subject = getStringFromJSON(jsonElement, "subject");
        String verb = getStringFromJSON(jsonElement, "verb");
        String complement = getStringFromJSON(jsonElement, "complement");
        String object = getStringFromJSON(jsonElement, "object");

        return null;
    }

    private String getStringFromJSON(JSONObject jsonElement, String target) {
        return jsonElement.get(target).toString();
    }

    private Boolean getBooleanFromJSON(JSONObject jsonElement, String target) {
        return Objects.equals(jsonElement.get(target).toString(), "true");
    }

//    private void testJBPT() {
//        MultiDirectedGraph g = new MultiDirectedGraph();
//
//        Vertex s = new Vertex("s");
//        Vertex u = new Vertex("u");
//        Vertex v = new Vertex("v");
//        Vertex w = new Vertex("w");
//        Vertex x = new Vertex("x");
//        Vertex t = new Vertex("t");
//
//        g.addEdge(s,u);
//        g.addEdge(u,v);
//        g.addEdge(v,x);
//        g.addEdge(u,w);
//        g.addEdge(w,x);
//        g.addEdge(v,w);
//        g.addEdge(x,t);
//
//        RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);
//        System.out.println(rpst);
//        System.out.println("test");
//    }

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
            System.out.println("JSON file with final sentence created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

