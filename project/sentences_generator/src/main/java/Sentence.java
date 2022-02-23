import java.util.ArrayList;

public class Sentence {
    String originalSentence;
    String finalSentence;
    ArrayList<String> actions;

    public Sentence(String originalSentence, String finalSentence, ArrayList<String> actions) {
        this.originalSentence = originalSentence;
        this.finalSentence = finalSentence;
        this.actions = actions;
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

    private void buildFinalSentence() {
    }
}

