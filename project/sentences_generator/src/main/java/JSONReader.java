import java.util.ArrayList;

public class JSONReader {
    String path;
    String originalSentence;
    String finalSentence;
    ArrayList<String> actions;

    public JSONReader(String path) {
        this.path = path;
    }

    public String getOriginalSentence() {
        return originalSentence;
    }

    public String getFinalSentence() {
        return finalSentence;
    }

    public ArrayList<String> getActions() {
        return actions;
    }

    public void setOriginalSentence(String originalSentence) {
        this.originalSentence = originalSentence;
    }

    public void setFinalSentence(String finalSentence) {
        this.finalSentence = finalSentence;
    }

    public void setActions(ArrayList<String> actions) {
        this.actions = actions;
    }

    public void buildFinalSentence() {
        SentenceGenerator generator = new SentenceGenerator(originalSentence);
        this.finalSentence = generator.getFinalSentence();
    }
}

