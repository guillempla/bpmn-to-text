import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.phrasespec.SPhraseSpec;

public class CoordinatedSentence extends Sentence {
    private final CoordinatedPhraseElement phrase;

    public CoordinatedSentence() {
        super();
        NLGFactory nlgFactory = new NLGFactory(super.lexicon);

        this.phrase = nlgFactory.createCoordinatedPhrase();
        this.phrase.setConjunction("then");
    }

    public void addCoordinateSentence(Sentence sentence) {
        String realizedSentence = sentence.sentenceToString();
        if (Character.isUpperCase(realizedSentence.charAt(0))) {
            realizedSentence = realizedSentence.toLowerCase();
        }
        if (sentence.getPhrase() instanceof SPhraseSpec) {
            phrase.addCoordinate(sentence);
        }
        else {
            String removedDotSentence = realizedSentence.replace(".", "");
            phrase.addCoordinate(removedDotSentence);
        }
    }
}
