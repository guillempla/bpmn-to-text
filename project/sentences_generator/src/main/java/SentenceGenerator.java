import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

import java.util.Map;

public class SentenceGenerator {
    String originalSentence;
    String lane;
    Map<String, String> actions;
    String finalSentence;

    public SentenceGenerator(String originalSentence, String lane, Map<String, String> actions) {
        this.originalSentence = originalSentence;
        this.lane = lane;
        this.actions = actions;
    }

    public String getFinalSentence() {
        return this.finalSentence;
    }
}
