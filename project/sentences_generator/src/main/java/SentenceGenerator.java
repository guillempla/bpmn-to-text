import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

import java.util.Map;

public class SentenceGenerator {
    Lexicon lexicon;
    NLGFactory nlgFactory;
    Realiser realiser;

    String originalSentence;
    String lane;
    Map<String, String> actions;
    String finalSentence;

    public SentenceGenerator(String originalSentence, String lane, Map<String, String> actions) {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);
        this.realiser = new Realiser(lexicon);

        this.originalSentence = originalSentence;
        this.lane = lane;
        this.actions = actions;
    }

    public String getFinalSentence() {
        return this.finalSentence;
    }

    private void generateFinalSentence() {
        SPhraseSpec phrase = nlgFactory.createClause();

        if (lane != null) {
            phrase.setSubject(lane);
        }

        String verb = searchVerb();
        phrase.setVerb(verb);

        String object = searchObject();
        if (object != null) {
            phrase.setObject(object);
        }

        this.finalSentence = realiser.realiseSentence(phrase);
    }

    private String searchVerb() {
        return "";
    }

    private String searchObject() {
        return "";
    }
}
