import java.util.ArrayList;
import org.json.simple.JSONObject;

public class JSONReader {
    String path;
    String finalSentence;
    ArrayList<JSONObject> jsonObjects;

    public JSONReader(String path) {
        this.path = path;
        this.readJSONObjects();
        for (JSONObject jsonObject : jsonObjects) {
            generateSentence(jsonObject);
        }
    }

    public void buildFinalSentence() {
        // print and save finalSentence to file
    }

    private void readJSONObjects() {
        // get every single json element
    }

    private String readOriginalSentence(JSONObject jsonObject) {
        String originalSentence = "";
        return originalSentence;
    }

    private String readLane(JSONObject jsonObject) {
        String lane = "";
        return lane;
    }

    private ArrayList<String> readActions(JSONObject jsonObject) {
        ArrayList<String> actions = new ArrayList<>();
        return actions;
    }

    private void generateSentence(JSONObject jsonObject) {
        String originalSentence = readOriginalSentence(jsonObject);
        String lane = readLane(jsonObject);
        ArrayList<String> actions = readActions(jsonObject);
        SentenceGenerator generator = new SentenceGenerator(originalSentence, lane, actions);
        this.finalSentence = generator.getFinalSentence();
    }
}

