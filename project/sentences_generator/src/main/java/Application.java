import java.io.File;
import java.util.ArrayList;

public class Application {

    public static void main(String[] args) {
        String responsesPath = "../freeling_responses/";
        if (args.length > 0) {
            responsesPath = args[0];
        }

        ArrayList<String> json_paths = getJSONPaths(responsesPath);
        for (String path: json_paths) {
            String bpmn_name = getJSONNameFromPath(path);
            System.out.println(bpmn_name);
            JSONReader reader = new JSONReader(path);
            reader.buildFinalSentence();
            System.out.println();
        }
    }

    private static ArrayList<String> getJSONPaths(String path) {
        ArrayList<String> paths = new ArrayList<>();

        File directoryPath = new File(path);
        //List of all files and directories
        File[] filesList = directoryPath.listFiles();
        if (filesList != null) {
            for (File file : filesList) {

                File filePath = new File(file.getAbsolutePath());
                //List of all files and directories
                paths.add(file.getAbsolutePath());
            }
        }
        return paths;
    }

    private static String getJSONNameFromPath(String path) {
        String[] paths_elements = path.split("/");
        return paths_elements[paths_elements.length-1].replace(".json", "");
    }
}
