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
            String removedDotSentence = realizedSentence.replace(".", "").replace("\n\n", "");
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

    public void printParagraph() {
        System.out.print(paragraphToString() + ", ");
    }

    public void printlnParagraph() {
        System.out.println(paragraphToString());
    }

    private String realizeSentence(NLGElement sentence) {
        try {
            return realiser.realise(sentence).getRealisation();
        } catch (Exception e) {
            return "";
        }
    }

    public void addCoordinateSentence(Sentence sentence, boolean branch) {
        String realizedSentence = sentence.paragraphToString();
        if (Character.isUpperCase(realizedSentence.charAt(0))) {
            realizedSentence = realizedSentence.toLowerCase();
        }

        if (branch && !sentence.isVoid()) {
            String removedDotSentence = realizedSentence.replace(".", "").replace("\n\n", "\n");
            NLGElement branchPhrase = nlgFactory.createSentence(removedDotSentence);
            sentence.setCoordinatedPhrase(branchPhrase);
            coordinatedPhrases.push(sentence.getPeekCoordinatedPhrase());
        }
        else {
            String removedDotSentence = realizedSentence.replace(".", "").replace("\n", "");
            coordinatedPhrases.peek().addCoordinate(removedDotSentence);
        }
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

    public void joinSentence(Sentence sentence, boolean branch) {
        if (sentence.getStackSize() > 1) {
            this.coordinatedPhrases.addAll(sentence.getCoordinatedPhrases());
            // TODO Afegir comprovació de frase massa llarga i crear una nova
            //  entrada a l'Stack
        } // TODO hi ha més casos a tenir en compte a part de "sentence.getStackSize() > 1"
        else {
            addCoordinateSentence(sentence, branch);
        }

        joinedVertex.addAll(sentence.getJoinedVertex());
    }

    public int getStackSize() {
        return this.coordinatedPhrases.size();
    }

    public Stack<CoordinatedPhraseElement> getCoordinatedPhrases() {
        return coordinatedPhrases;
    }

    public boolean isVoid() {
        return coordinatedPhrases == null || paragraphToString().replace("\n", "").equals("");
    }

    public CoordinatedPhraseElement getPeekCoordinatedPhrase() {
        return coordinatedPhrases.peek();
    }

    public void setCoordinatedPhrase(NLGElement phrase) {
        CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        coordinatedPhrase.setConjunction("then");
        if (!coordinatedPhrases.empty()) coordinatedPhrases.pop();
        coordinatedPhrases.push(coordinatedPhrase);
        addCoordinateSentence(phrase);
    }

    public String sentenceToString() {
        return realizeSentence(coordinatedPhrases.peek());
    }

    public String paragraphToString() {
        return realizeSentence(this.getParagraph());
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
