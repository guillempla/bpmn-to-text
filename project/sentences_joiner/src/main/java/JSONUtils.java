import org.json.simple.JSONObject;

import java.util.Objects;

public class JSONUtils {
    static String getStringFromJSON(JSONObject jsonElement, String target) {
        try {
            return jsonElement.get(target).toString();
        } catch (NullPointerException e) {
//            System.out.println("NullPointerException: " + target + " is null!");
            return null;
        }
    }

    static Boolean getBooleanFromJSON(JSONObject jsonElement, String target) {
        try {
            return Objects.equals(jsonElement.get(target).toString(), "true");
        } catch (NullPointerException e) {
//            System.out.println("NullPointerException: " + target + " is null!");
            return null;
        }
    }
}
