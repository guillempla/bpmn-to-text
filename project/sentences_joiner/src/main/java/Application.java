import java.io.File;
import java.util.ArrayList;

public class Application {

    public static void main(String[] args) {
        String sentencesPath = "../sentences_generated/";
        if (args.length > 0) {
            sentencesPath = args[0];
        }

        ArrayList<String> jsonPaths = getJSONPaths(sentencesPath);
        for (String path: jsonPaths) {
            String bpmnName = getJSONNameFromPath(path);
            if (bpmnName.equals("cook.1") || true) {
                JSONReader reader = new JSONReader(bpmnName, path);
                //reader.saveJSON();
                System.out.println();
            }
        }
    }

    private static ArrayList<String> getJSONPaths(String path) {
        ArrayList<String> paths = new ArrayList<>();

        File directoryPath = new File(path);
        //List of all files and directories
        File[] filesList = directoryPath.listFiles();
        if (filesList != null) {
            for (File file : filesList) {
                paths.add(file.getAbsolutePath());
            }
        }
        return paths;
    }

    private static String getJSONNameFromPath(String path) {
        String[] pathsElements = path.split("/");
        return pathsElements[pathsElements.length-1].replace(".json", "");
    }
}
