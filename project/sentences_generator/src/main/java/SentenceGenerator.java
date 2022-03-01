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

        System.out.println(this.originalSentence);
        System.out.println(this.actions);

        this.finalSentence = this.originalSentence != null ? this.generateFinalSentence() : null;
    }

    public String getFinalSentence() {
        return this.finalSentence;
    }

    private String generateFinalSentence() {
        if (actions == null) {
            return generateWithoutActions();
        }
        else {
            return generateWithActions();
        }
    }

    private String generateWithoutActions() {
        NLGElement phrase = nlgFactory.createSentence(originalSentence);

        if (originalSentence.contains("?")) {
            phrase.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
        }

        return realiser.realiseSentence(phrase);
    }

    private String generateWithActions() {
        SPhraseSpec phrase = nlgFactory.createClause();

        if (lane != null) {
            phrase.setSubject(lane);
        }
        else {
            phrase.setFeature(Feature.PASSIVE, true);
        }

        String verb = searchVerb();
        if (verb != null) {
            phrase.setVerb(verb);
        }

        String object = searchObject();
        if (object != null) {
            phrase.setObject(object);
        }

        String complement = searchComplement();
        if (complement != null) {
            phrase.setComplement(complement);
        }

        return realiser.realiseSentence(phrase);
    }

    private String searchVerb() {
        return this.actions != null ? this.actions.get("predL") : null;
    }

    private String searchObject() {
        return this.actions != null ? this.actions.get("objL") : null;
    }

    private String searchComplement() {
        return this.actions != null ? this.actions.get("compW") : null;
    }
}
