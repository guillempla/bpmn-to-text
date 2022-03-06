import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {
    public static final String GENERATED_PATH = "../sentences_generated/";

    String fileName;
    String path;
    String finalSentence;
    JSONObject jsonElements;

    public JSONReader(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
        this.jsonElements = parseJSON(this.path);
        for (Object key : jsonElements.keySet()) {
            JSONObject jsonElement = (JSONObject) jsonElements.get(key);
            System.out.println(jsonElement.get("type"));
            this.generateSentence(jsonElement);
            this.buildFinalSentences(jsonElement);
            System.out.println();
        }
    }

    private void buildFinalSentences(JSONObject jsonElement) {
        jsonElement.put("finalSentence", this.finalSentence);
        System.out.println(this.finalSentence);
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

    private String readOriginalSentence(JSONObject jsonObject) {
        return (String) jsonObject.get("name");
    }

    private String readLane(JSONObject jsonObject) {
        return (String) jsonObject.get("lane");
    }

    private Map<String, String> readActions(JSONObject jsonObject) {
        return (Map<String, String>) jsonObject.get("actions");
    }

    private void generateSentence(JSONObject jsonObject) {
        String originalSentence = readOriginalSentence(jsonObject);
        String lane = readLane(jsonObject);
        Map<String, String> actions = readActions(jsonObject);
        SentenceGenerator generator = new SentenceGenerator(originalSentence, lane, actions);
        this.finalSentence = generator.getFinalSentence();
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

