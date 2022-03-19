import org.json.simple.JSONObject;

import java.util.Objects;

public class JSONUtils {
    static String getStringFromJSON(JSONObject jsonElement, String target) {
        return jsonElement.get(target).toString();
    }

    static Boolean getBooleanFromJSON(JSONObject jsonElement, String target) {
        return Objects.equals(jsonElement.get(target).toString(), "true");
    }
}
