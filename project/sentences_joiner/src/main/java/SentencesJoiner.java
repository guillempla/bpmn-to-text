import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;

public class SentencesJoiner {
    Lexicon lexicon;
    NLGFactory nlgFactory;
    Realiser realiser;

    private final String commaJoin = ", ";
    private final String dotJoin = ". ";

    public SentencesJoiner() {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);
        this.realiser = new Realiser(lexicon);
    }

    public String sentenceToString(NLGElement sentence) {
        return realizeSentence(sentence);
    }

    private String realizeSentence(NLGElement sentence) {
        try {
            return realiser.realiseSentence(sentence);
        } catch (Exception e) {
            return "";
        }
    }

    public NLGElement joinSentences(ElementVertex vertex, ArrayList<NLGElement> sentences) {
        if (vertex.isGate()) {
            return joinGateways(vertex.getType(), sentences);
        }
        return joinActivities(vertex.getType(), sentences);
    }

    private NLGElement joinGateways(String gatewayType, ArrayList<NLGElement> sentences) {
        CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        coordinatedPhrase.setConjunction("then");
        sentences.removeIf(this::sentenceIsVoid);
        addCoordinateSentence(coordinatedPhrase, sentences.get(0));
        sentences.remove(0);
        for (NLGElement sentence : sentences) {
            int totalWordCount = countWords(sentence) + countWords(coordinatedPhrase);
            if (totalWordCount < 50) {
                addCoordinateSentence(coordinatedPhrase, sentence);
            }
            else {
                addCoordinateSentence(coordinatedPhrase, sentence);
            }
        }
        return coordinatedPhrase;
    }

    private NLGElement joinActivities(String type, ArrayList<NLGElement> sentences) {
        CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        coordinatedPhrase.setConjunction("then");
        sentences.removeIf(this::sentenceIsVoid);
        if (sentences.size() == 0) return null;

        addCoordinateSentence(coordinatedPhrase, sentences.get(0));
        sentences.remove(0);
        for (NLGElement sentence : sentences) {
            int totalWordCount = countWords(sentence) + countWords(coordinatedPhrase);
            if (totalWordCount < 50) {
                addCoordinateSentence(coordinatedPhrase, sentence);
            }
            else {
                addCoordinateSentence(coordinatedPhrase, sentence);
            }
        }
        return coordinatedPhrase;
    }

    private boolean sentenceIsVoid(NLGElement sentence) {
        return sentence == null || realizeSentence(sentence).equals("");
    }

    private void addCoordinateSentence(CoordinatedPhraseElement coordinatedPhrase, NLGElement sentence) {
        String realizedSentence = realizeSentence(sentence);
        if (Character.isUpperCase(realizedSentence.charAt(0))) {
            realizedSentence = realizedSentence.toLowerCase();
        }
        if (sentence instanceof SPhraseSpec) {
            coordinatedPhrase.addCoordinate(sentence);
        }
        else {
            String removedDotSentence = realizedSentence.replace(".", "");
            coordinatedPhrase.addCoordinate(removedDotSentence);
        }
    }

    private int countWords(NLGElement element) {
        String sentence = realizeSentence(element);
        if (sentence.isEmpty()) {
            return 0;
        }

        String[] words = sentence.split("\\s+");
        return words.length;
    }
}
