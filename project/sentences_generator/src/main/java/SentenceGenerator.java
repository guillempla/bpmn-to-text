import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

import java.util.Map;
import java.util.Objects;

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

        if (originalSentence.contains("?")) {
            return generateQuestionWithActions(phrase);
        }

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

        NPPhraseSpec object = searchObject();
        if (object != null) {
            phrase.setObject(object);
        }

        String complement = searchComplement();
        if (complement != null) {
            phrase.addModifier(complement); // Complement has to be added as modified to
        }

        return realiser.realiseSentence(phrase);
    }

    private String generateQuestionWithActions(SPhraseSpec phrase) {
        phrase.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);

        String verb = searchVerb();
        if (verb != null) {
            phrase.setVerb(verb);
        }

        NPPhraseSpec object = searchObject();
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

    private NPPhraseSpec searchObject() {
        if (this.actions == null) {
            return null;
        }

        String object = this.actions.get("objW");
        NPPhraseSpec objectPhrase = nlgFactory.createNounPhrase(object);
        String objMSD = this.actions.get("objMSD");
        if (objMSD != null) {
            String[] objMSDSplit = objMSD.split("\\|");
            if (objMSDSplit.length > 2) {
                String morfologicalInfo = objMSDSplit[2];
                String num = morfologicalInfo.split("=")[1];
                if (Objects.equals(num, "plural")) {
                    objectPhrase.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                }
            }
        }

        // Add determiner "the", only if it doesn't start by "the" or "for"
        if (object != null) {
            String firstWord = object.split(" ")[0];
            if (!firstWord.equals("the") && !firstWord.equals("for") && !wordIsGerund(firstWord)) {
                objectPhrase.setDeterminer("the");
            }
        }

        return objectPhrase;
    }

    private boolean wordIsGerund(String firstWord) {
        return firstWord.contains("ing");
    }

    private String searchComplement() {
        return this.actions != null ? this.actions.get("compW") : null;
    }
}
