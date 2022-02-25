import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gov.nih.nlm.nls.lvg.Util.Str;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {
    String path;
    String finalSentence;
    JSONObject jsonElements;

    public JSONReader(String path) {
        this.path = path;
        this.jsonElements = parseJSON(this.path);
        for (Object key : jsonElements.keySet()) {
            JSONObject jsonElement = (JSONObject) jsonElements.get(key);
            generateSentence(jsonElement);
        }
    }

    public void buildFinalSentence() {
        // print and save finalSentence to file
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
}

