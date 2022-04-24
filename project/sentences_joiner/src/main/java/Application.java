import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Application {

    public static void main(String[] args) {
        String sentencesPath = "../sentences_generated/";
        if (args.length > 0) {
            sentencesPath = args[0];
        }

        ArrayList<String> jsonPaths = getJSONPaths(sentencesPath);
        for (String path: jsonPaths) {
            String bpmnName = getJSONNameFromPath(path);
            ArrayList<String> namesDifferentSize = new ArrayList<>(Arrays.asList("C.6.0.1", "A.2.0.1",
                    "A.4.1.3", "B.2.0.3", "C.1.1.1", "B.2.0.7", "C.4.0.1", "B.1.0.3", "Order Fulfillment and Procurement.3",
                    "B.2.0.6", "A.4.0.2", "C.1.0.2"));
            ArrayList<String> gateNoChild = new ArrayList<>(Arrays.asList("A.2.0.1", "C.3.0.1",
                    "Order Fulfillment and Procurement.3", "B.2.0.6"));
            ArrayList<String> rigids = new ArrayList<>(Arrays.asList("C.2.0.4", "C.3.0.1", "C.1.1.1", "B.2.0.6", "A.2.1.1",
                    "C.1.0.2", "C.5.0.1"));
            if (bpmnName.equals("A.4.0.2") /*namesDifferentSize.contains(bpmnName)*/ /* bpmnName.equals("cook.1")*/
                /*!namesDifferentSize.contains(bpmnName) && !gateNoChild.contains(bpmnName)*/) {
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
