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
        if (args.length > 0) {
            sentencesPath = args[0];
        }

        ArrayList<String> jsonPaths = getJSONPaths(sentencesPath);
        for (String path: jsonPaths) {
            String bpmnName = getJSONNameFromPath(path);
            ArrayList<String> gateNoChild = new ArrayList<>(List.of("Order Fulfillment and Procurement.3"));
            ArrayList<String> rigids = new ArrayList<>(Arrays.asList("C.2.0.4", "C.3.0.1", "C.1.1.1", "B.2.0.6", "A.2.1.1",
                    "C.1.0.2", "C.5.0.1"));
            if (true || bpmnName.equals("cook.1") /*&& !gateNoChild.contains(bpmnName)*/ /*&& !rigids.contains(bpmnName)*/
                /*!namesDifferentSize.contains(bpmnName) && !gateNoChild.contains(bpmnName)*/) {
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
