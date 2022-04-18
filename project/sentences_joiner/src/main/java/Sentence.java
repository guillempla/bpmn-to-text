import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;

public class Sentence {
    protected final Realiser realiser;
    private boolean isFirstGateway;
    private ArrayList<ElementVertex> joinedVertex;
    private final NLGFactory nlgFactory;
    protected final Lexicon lexicon;
    private CoordinatedPhraseElement coordinatedPhrase;

    public Sentence() {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.realiser = new Realiser(lexicon);
        this.nlgFactory = new NLGFactory(lexicon);

        this.joinedVertex = new ArrayList<>();
        this.isFirstGateway = false;

        this.coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        this.coordinatedPhrase.setConjunction("then");
    }

    public Sentence(NLGElement phrase, ElementVertex vertex) {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.realiser = new Realiser(lexicon);
        this.nlgFactory = new NLGFactory(lexicon);

        this.joinedVertex = new ArrayList<>();
        this.joinedVertex.add(vertex);
        this.isFirstGateway = vertex.isOpenGateway();

        this.coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        this.coordinatedPhrase.setConjunction("then");

        addCoordinateSentence(phrase);
    }

    public void addCoordinateSentence(NLGElement sentence) {
        String realizedSentence = realizeSentence(sentence);
        if (realizedSentence.equals("")) {
            return;
        }
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

    public boolean isFirstGateway() {
        return isFirstGateway;
    }

    public boolean onlyOneGateway() {
        return joinedVertex.size() == 1 && joinedVertex.get(0).isOpenGateway();
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

    private String realizeSentence(NLGElement sentence) {
        try {
            return realiser.realise(sentence).getRealisation();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isVoid() {
        return coordinatedPhrase == null || sentenceToString().equals("");
    }

    public int numWords() {
        String sentence = sentenceToString();
        if (sentence.isEmpty()) {
            return 0;
        }

        String[] words = sentence.split("\\s+");
        return words.length;
    }

    public String sentenceToString() {
        return realizeSentence(coordinatedPhrase);
    }

    public void addCoordinateSentence(Sentence sentence) {
        // TODO Afegir els vertexs
        String realizedSentence = sentence.sentenceToString();
        if (Character.isUpperCase(realizedSentence.charAt(0))) {
            realizedSentence = realizedSentence.toLowerCase();
        }
        if (sentence.getCoordinatedPhrase() instanceof SPhraseSpec) {
            coordinatedPhrase.addCoordinate(sentence);
        }
        else {
            String removedDotSentence = realizedSentence.replace(".", "");
            coordinatedPhrase.addCoordinate(removedDotSentence);
        }

        joinedVertex.addAll(sentence.getJoinedVertex());

        // TODO Comprovar si cal afegir una coordinatedPhrase com a tal
        //  (ara s'afegeix com a String)
    }

    public NLGElement getCoordinatedPhrase() {
        return coordinatedPhrase;
    }

    public Sentence copySentence() {
        Sentence copy = new Sentence();
        copy.setCoordinatedPhrase(this.coordinatedPhrase);
        copy.setFirstGateway(this.isFirstGateway);
        copy.setJoinedVertex(this.joinedVertex);

        return copy;
    }

    public void setCoordinatedPhrase(CoordinatedPhraseElement coordinatedPhrase) {
        this.coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        this.coordinatedPhrase.setConjunction("then");
        addCoordinateSentence(coordinatedPhrase);
    }

    public void setCoordinatedPhrase(NLGElement phrase) {
        this.coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        this.coordinatedPhrase.setConjunction("then");
        addCoordinateSentence(phrase);
    }
}
