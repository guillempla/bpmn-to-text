import org.json.simple.JSONObject;
import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.NumberAgreement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;

public class PhraseRetriever {
    private final NLGFactory nlgFactory;

    private final String originalSentence;
    private final String finalSentence;
    private final NLGElement phrase;
    private Boolean objectDeterminant;
    private Boolean interrogative;
    private Boolean objectPlural;
    private Boolean existActions;
    private String subject;
    private String verb;
    private String complement;
    private String object;

    public PhraseRetriever(String originalSentence, String finalSentence, JSONObject jsonElement) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);

        this.originalSentence = originalSentence;
        this.finalSentence = finalSentence;

        this.initializePhraseAttributes(jsonElement);
        this.phrase = this.retrievePhrase();
    }

    public NLGElement getPhrase() {
        return this.phrase;
    }

    private void initializePhraseAttributes(JSONObject jsonElement) {
        this.objectDeterminant = JSONUtils.getBooleanFromJSON(jsonElement, "objectDeterminant");
        this.interrogative = JSONUtils.getBooleanFromJSON(jsonElement, "interrogative");
        this.objectPlural = JSONUtils.getBooleanFromJSON(jsonElement, "objectPlural");
        this.existActions = JSONUtils.getBooleanFromJSON(jsonElement, "existActions");
        this.subject = JSONUtils.getStringFromJSON(jsonElement, "subject");
        this.verb = JSONUtils.getStringFromJSON(jsonElement, "verb");
        this.complement = JSONUtils.getStringFromJSON(jsonElement, "complement");
        this.object = JSONUtils.getStringFromJSON(jsonElement, "object");
    }

    private NLGElement retrievePhrase() {
        if (finalSentence == null) {
            return null;
        }
        if (existActions) {
            return generateWithActions();
        }
        return generateWithoutActions();
    }

    private SPhraseSpec generateWithActions() {
        SPhraseSpec phrase = nlgFactory.createClause();

        if (interrogative) {
            return generateQuestionWithActions(phrase);
        }

        if (subject != null) {
            phrase.setSubject(subject);
        } else {
            phrase.setFeature(Feature.PASSIVE, true);
        }

        if (verb != null) {
            phrase.setVerb(verb);
        }

        if (object != null) {
            NPPhraseSpec objectPhrase = nlgFactory.createNounPhrase(object);
            if (objectPlural) {
                objectPhrase.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            }
            if (objectDeterminant) {
                objectPhrase.setDeterminer("the");
            }
            phrase.setObject(objectPhrase);
        }

        if (complement != null) {
            phrase.addModifier(complement); // Complement has to be added as modified to
        }

        return phrase;
    }

    private NLGElement generateWithoutActions() {
        NLGElement phrase = nlgFactory.createSentence(originalSentence);

        if (interrogative) {
            phrase.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
        }

        return phrase;
    }

    private SPhraseSpec generateQuestionWithActions(SPhraseSpec phrase) {
        phrase.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);

        if (verb != null) {
            phrase.setVerb(verb);
        }

        if (object != null) {
            NPPhraseSpec objectPhrase = nlgFactory.createNounPhrase(object);
            if (objectPlural) {
                objectPhrase.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            }
            if (objectDeterminant) {
                objectPhrase.setDeterminer("the");
            }
            phrase.setObject(objectPhrase);
        }

        if (complement != null) {
            phrase.addModifier(complement); // Complement has to be added as modified to
        }

        return phrase;
    }

}
