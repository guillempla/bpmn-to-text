import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

import java.util.HashMap;
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

    String subject;
    String verb;
    String object;
    String complement;
    String interrogative;
    String objectPlural;
    String objectDeterminant;
    String existActions;

    public SentenceGenerator(String originalSentence, String lane, Map<String, String> actions) {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);
        this.realiser = new Realiser(lexicon);

        this.originalSentence = originalSentence;
        this.lane = lane;
        this.actions = actions;

        System.out.println(this.originalSentence);
        System.out.println(this.actions);

        this.initializeFinalPhraseAttributes();

        this.finalSentence = this.originalSentence != null ? this.generateFinalSentence() : null;
    }

    public String getFinalSentence() {
        return this.finalSentence;
    }

    public Map<String, String> getFinalPhraseAttributes() {
        Map<String, String> finalPhraseAttributes = new HashMap<>();

        finalPhraseAttributes.put("subject", subject);
        finalPhraseAttributes.put("verb", verb);
        finalPhraseAttributes.put("object", object);
        finalPhraseAttributes.put("complement", complement);
        finalPhraseAttributes.put("interrogative", interrogative);
        finalPhraseAttributes.put("objectPlural", objectPlural);
        finalPhraseAttributes.put("objectDeterminant", objectDeterminant);
        finalPhraseAttributes.put("existActions", existActions);

        return finalPhraseAttributes;
    }

    private void initializeFinalPhraseAttributes() {
        this.subject = null;
        this.verb = null;
        this.object = null;
        this.complement = null;
        this.interrogative = "false";
        this.objectPlural = "false";
        this.objectDeterminant = "false";
        this.existActions = "false";
    }

    private String generateFinalSentence() {

        if (actions == null) {
            this.existActions = "false";
            return generateWithoutActions();
        }
        else {
            this.existActions = "true";
            return generateWithActions();
        }
    }

    private String generateWithoutActions() {
        NLGElement phrase = nlgFactory.createSentence(originalSentence);

        if (originalSentence.contains("?")) {
            this.interrogative = "true";
            phrase.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
        }

        return realiser.realiseSentence(phrase);
    }

    private String generateWithActions() {
        SPhraseSpec phrase = nlgFactory.createClause();

        if (originalSentence.contains("?")) {
            this.interrogative = "true";
            return generateQuestionWithActions(phrase);
        }

        if (lane != null) {
            this.subject = lane;
            phrase.setSubject(lane);
        }
        else {
            phrase.setFeature(Feature.PASSIVE, true);
        }

        String verb = searchVerb();
        this.verb = verb;
        if (verb != null) {
            phrase.setVerb(verb);
        }

        NPPhraseSpec object = searchObject();
        if (object != null) {
            phrase.setObject(object);
        }

        String complement = searchComplement();
        this.complement = complement;
        if (complement != null) {
            phrase.addModifier(complement); // Complement has to be added as modified to
        }

        return realiser.realiseSentence(phrase);
    }

    private String generateQuestionWithActions(SPhraseSpec phrase) {
        phrase.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);

        String verb = searchVerb();
        this.verb = verb;
        if (verb != null) {
            phrase.setVerb(verb);
        }

        NPPhraseSpec object = searchObject();
        if (object != null) {
            phrase.setObject(object);
        }

        String complement = searchComplement();
        this.complement = complement;
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
        this.object = object;
        NPPhraseSpec objectPhrase = nlgFactory.createNounPhrase(object);
        String objMSD = this.actions.get("objMSD");
        if (objMSD != null) {
            String[] objMSDSplit = objMSD.split("\\|");
            if (objMSDSplit.length > 2) {
                String morfologicalInfo = objMSDSplit[2];
                String num = morfologicalInfo.split("=")[1];
                if (Objects.equals(num, "plural")) {
                    this.objectPlural = "true";
                    objectPhrase.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                }
            }
        }

        // Add determiner "the", only if it doesn't start by "the" or "for"
        if (object != null) {
            String firstWord = object.split(" ")[0];
            if (needsDeterminer(firstWord)) {
                this.objectDeterminant = "true";
                objectPhrase.setDeterminer("the");
            }
        }

        return objectPhrase;
    }

    private boolean needsDeterminer(String word) {
        return !word.equals("the") && !word.equals("for") && !wordIsGerund(word);
    }

    private boolean wordIsGerund(String firstWord) {
        return firstWord.contains("ing");
    }

    private String searchComplement() {
        return this.actions != null ? this.actions.get("compW") : null;
    }
}
