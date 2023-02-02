import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        String sentencesPath = "../sentences_generated/";
        String joinedPath = "../sentences_joined/";
        if (args.length == 1) {
            sentencesPath = args[0];
        }
        else if (args.length == 2) {
            sentencesPath = args[0];
            joinedPath = args[1];
        }

        ArrayList<String> jsonPaths = getJSONPaths(sentencesPath);
        for (String path : jsonPaths) {
            String bpmnName = getJSONNameFromPath(path);
            ArrayList<String> gateNoChild = new ArrayList<>(List.of("Order Fulfillment and Procurement.3"));
            ArrayList<String> rigids = new ArrayList<>(Arrays.asList("C.2.0.4", "C.3.0.1", "C.1.1.1", "B.2.0.6", "A.2.1.1",
                    "C.1.0.2", "C.5.0.1"));
            ArrayList<String> noSatisfactori = new ArrayList<>(Arrays.asList(
                    "C.7.0.1",
                    "Order Fulfillment and Procurement.1",
                    "Order Fulfillment and Procurement.2",
                    "Order Fulfillment and Procurement.3",
                    "Potsdam.1",
                    "Potsdam.2",
                    "Wuerzburg.1",
                    "Wuerzburg.2"));
            ArrayList<String> noReals = new ArrayList<>(Arrays.asList(
                    "A.1.0.1",
                    "A.2.0.1",
                    "A.2.1.1",
                    "A.3.0.1",
                    "A.4.0.1",
                    "A.4.0.2",
                    "A.4.0.3",
                    "A.4.0.4",
                    "A.4.1.1",
                    "A.4.1.2",
                    "A.4.1.3",
                    "A.4.1.4",
                    "B.1.0.1",
                    "B.1.0.2",
                    "B.1.0.3",
                    "B.1.0.4",
                    "B.1.0.5",
                    "B.2.0.1",
                    "B.2.0.2",
                    "B.2.0.3",
                    "B.2.0.4",
                    "B.2.0.5",
                    "B.2.0.6",
                    "B.2.0.7",
                    "B.2.0.8",
                    "B.2.0.9",
                    "model_01.1"));
            if (!gateNoChild.contains(bpmnName) && !noReals.contains(bpmnName) && !rigids.contains(bpmnName) &&
            !noSatisfactori.contains(bpmnName)) {
                JSONReader reader = new JSONReader(bpmnName, path);
                String paragraph = reader.getParagraph();
                saveParagraph(joinedPath, bpmnName, paragraph);
                System.out.println();
            }
        }
    }

    private static void saveParagraph(String path, String fileName, String paragraph) {
        try (PrintStream out = new PrintStream(new FileOutputStream(path + fileName + ".txt"))) {
            out.print(paragraph);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
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
