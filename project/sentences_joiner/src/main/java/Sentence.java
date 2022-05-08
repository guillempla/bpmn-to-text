import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;
import java.util.Stack;

public class Sentence {
    protected final Realiser realiser;
    private boolean isFirstGateway;
    private ArrayList<ElementVertex> joinedVertex;
    private final NLGFactory nlgFactory;
    private final Lexicon lexicon;
    private final Stack<CoordinatedPhraseElement> coordinatedPhrases;

    public Sentence() {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.realiser = new Realiser(lexicon);
        this.nlgFactory = new NLGFactory(lexicon);

        this.joinedVertex = new ArrayList<>();
        this.isFirstGateway = false;

        coordinatedPhrases = new Stack<>();
        CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        coordinatedPhrase.setConjunction("then");
        coordinatedPhrases.push(coordinatedPhrase);
    }

    public Sentence(NLGElement phrase, ElementVertex vertex) {
        this.lexicon = Lexicon.getDefaultLexicon();
        this.realiser = new Realiser(lexicon);
        this.nlgFactory = new NLGFactory(lexicon);

        this.joinedVertex = new ArrayList<>();
        this.joinedVertex.add(vertex);
        this.isFirstGateway = vertex.isOpenGateway();

        coordinatedPhrases = new Stack<>();
        CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        coordinatedPhrase.setConjunction("then");
        coordinatedPhrases.push(coordinatedPhrase);

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
            coordinatedPhrases.peek().addCoordinate(sentence);
        }
        else {
            String removedDotSentence = realizedSentence.replace(".", "");
            coordinatedPhrases.peek().addCoordinate(removedDotSentence);
        }
    }

    public String getIdOfFirstJoinedVertex() {
        return joinedVertex.get(0).getElementId();
    }

    public boolean isFirstGateway() {
        return isFirstGateway;
    }

    public boolean onlyOneBifurcation() {
        return joinedVertex.size() == 1 && joinedVertex.get(0).isBifurcation();
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
        // TODO Crec que no funcione amb l'Stack
        System.out.print(sentenceToString() + ", ");
    }

    public void printlnSentence() {
        // TODO Crec que no funcione amb l'Stack
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
        return coordinatedPhrases.peek() == null || sentenceToString().equals("");
    }

    public int numWords() {
        // TODO No funcione amb l'Stack
        String sentence = sentenceToString();
        if (sentence.isEmpty()) {
            return 0;
        }

        String[] words = sentence.split("\\s+");
        return words.length;
    }

    public void addCoordinateSentence(Sentence sentence) {
        String realizedSentence = sentence.sentenceToString();
        if (Character.isUpperCase(realizedSentence.charAt(0))) {
            realizedSentence = realizedSentence.toLowerCase();
        }
        if (sentence.getPeekCoordinatedPhrase() instanceof SPhraseSpec) {
            coordinatedPhrases.peek().addCoordinate(sentence);
        }
        else {
            String removedDotSentence = realizedSentence.replace(".", "");
            coordinatedPhrases.peek().addCoordinate(removedDotSentence);
        }

        joinedVertex.addAll(sentence.getJoinedVertex());

        // TODO Comprovar si cal afegir una coordinatedPhrase com a tal
        //  (ara s'afegeix com a String)

        // TODO Afegir comprovació de frase massa llarga i crear una nova
        //  entrada a l'Stack
    }

    public NLGElement getPeekCoordinatedPhrase() {
        return coordinatedPhrases.peek();
    }

    public void setCoordinatedPhrase(NLGElement phrase) {
        CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        coordinatedPhrase.setConjunction("then");
        if (!coordinatedPhrases.empty()) coordinatedPhrases.pop();
        coordinatedPhrases.push(coordinatedPhrase);
        addCoordinateSentence(phrase);
    }

    private DocumentElement getParagraph() {
        Stack<DocumentElement> auxCoordinated = convertStack();
        return nlgFactory.createParagraph(auxCoordinated);
    }

    private Stack<DocumentElement> convertStack() {
        Stack<DocumentElement> auxCoordinated = new Stack<>();
        for (CoordinatedPhraseElement phrase : coordinatedPhrases) {
            auxCoordinated.push(convertCoordinatedToDocument(phrase));
        }
        return auxCoordinated;
    }

    private DocumentElement convertCoordinatedToDocument(CoordinatedPhraseElement phrase) {
        return nlgFactory.createSentence(phrase);
    }
}
