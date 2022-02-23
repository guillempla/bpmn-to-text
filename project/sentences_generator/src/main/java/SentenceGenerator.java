import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

import java.util.ArrayList;

public class SentenceGenerator {
    String originalSentence;
    String finalSentence;
    String lane;
    ArrayList<String> actions;

    public SentenceGenerator(String originalSentence, ArrayList<String> actions) {
        this.originalSentence = originalSentence;
        this.actions = actions;
    }

    public String getFinalSentence() {
        return this.finalSentence;
    }
}
