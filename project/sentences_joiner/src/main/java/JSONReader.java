import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import simplenlg.framework.NLGElement;

public class JSONReader {
    public static final String GENERATED_PATH = "../sentences_joined/";

    String fileName;
    String path;

    ArrayList<NLGElement> finalPhrases;
    JSONObject jsonElements;

    public JSONReader(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
        this.jsonElements = parseJSON(this.path);
        for (Object key : jsonElements.keySet()) {
            JSONObject jsonElement = (JSONObject) jsonElements.get(key);
            System.out.println(jsonElement.get("type"));

            System.out.println();
        }
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
            System.out.println("JSON file with final sentence created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

