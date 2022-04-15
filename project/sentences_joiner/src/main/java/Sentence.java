import simplenlg.framework.NLGElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;

public class Sentence {
    private NLGElement phrase;
    private boolean isFirstGateway;
    private ArrayList<ElementVertex> joinedVertex;

    private final Realiser realiser;
    protected final Lexicon lexicon;

    public Sentence() {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.realiser = new Realiser(lexicon);

        this.isFirstGateway = false;
        this.joinedVertex = new ArrayList<>();
    }

    public Sentence(NLGElement phrase) {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.realiser = new Realiser(lexicon);

        this.phrase = phrase;
        this.isFirstGateway = false;
        this.joinedVertex = new ArrayList<>();
    }

    public NLGElement getPhrase() {
        return phrase;
    }

    public void setPhrase(NLGElement phrase) {
        this.phrase = phrase;
    }

    public boolean isFirstGateway() {
        return isFirstGateway;
    }

    public void setFirstGateway(boolean firstGateway) {
        isFirstGateway = firstGateway;
    }

    public ArrayList<ElementVertex> getJoinedVertex() {
        return joinedVertex;
    }

    public void setJoinedVertex(ArrayList<ElementVertex> joinedVertex) {
        this.joinedVertex = joinedVertex;
    }

    public void addJoinedVertex(ElementVertex vertex) {
        joinedVertex.add(vertex);
    }

    public void printSentence() {
        System.out.println(sentenceToString());
    }

    public String sentenceToString() {
        try {
            return realiser.realiseSentence(phrase);
        } catch (Exception e) {
            return "";
        }
    }

    public int numWords() {
        String sentence = sentenceToString();
        if (sentence.isEmpty()) {
            return 0;
        }

        String[] words = sentence.split("\\s+");
        return words.length;
    }

    public boolean isVoid() {
        return phrase == null || sentenceToString().equals("");
    }
}
