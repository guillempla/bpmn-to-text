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
    protected Realiser realiser;
    private boolean isFirstGateway;
    private ArrayList<ElementVertex> joinedVertex;
    private NLGFactory nlgFactory;
    private Stack<CoordinatedPhraseElement> coordinatedPhrases;
    private String conjunction;

    public Sentence() {
        Conjunction conjunctionConnector = new Conjunction();
        this.conjunction = conjunctionConnector.getSelectedConnector();
        initializeSentence();

        this.joinedVertex = new ArrayList<>();
        this.isFirstGateway = false;
    }

    private void initializeSentence() {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.realiser = new Realiser(lexicon);
        this.nlgFactory = new NLGFactory(lexicon);

        coordinatedPhrases = new Stack<>();
        CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        coordinatedPhrase.setConjunction(conjunction);
        coordinatedPhrases.push(coordinatedPhrase);
    }

    public Sentence(NLGElement phrase, ElementVertex vertex) {
        Conjunction conjunctionConnector = new Conjunction();
        this.conjunction = conjunctionConnector.getSelectedConnector();
        initializeSentence();

        this.joinedVertex = new ArrayList<>();
        this.joinedVertex.add(vertex);
        this.isFirstGateway = vertex.isOpenGateway();

        addCoordinateSentence(phrase);
    }

    public Sentence(String conjunction) {
        this.conjunction = conjunction;
        initializeSentence();

        this.joinedVertex = new ArrayList<>();
        this.isFirstGateway = false;
    }

    public Sentence(NLGElement phrase, ElementVertex vertex, String conjunction) {
        this.conjunction = conjunction;
        initializeSentence();

        this.joinedVertex = new ArrayList<>();
        this.joinedVertex.add(vertex);
        this.isFirstGateway = vertex.isOpenGateway();

        addCoordinateSentence(phrase);
    }

    public void setConjunction(String conjunction) {
        this.conjunction = conjunction;
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

    public boolean isInitial() {
        return joinedVertex.get(0).isInitial();
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

    public int numWords() {
        // TODO No funcione amb l'Stack
        String sentence = lastSentenceToString();
        if (sentence.isEmpty()) {
            return 0;
        }

        String[] words = sentence.split("\\s+");
        return words.length;
    }

    public String lastSentenceToString() {
        return realizeSentence(coordinatedPhrases.peek());
    }

    public void joinSentence(Sentence sentence, boolean branch) {
        if (sentence.getStackSize() > 1) {
            sentence.getCoordinatedPhrases().removeIf(coordinatedPhrase -> realizeSentence(coordinatedPhrase).equals(""));
            this.coordinatedPhrases.addAll(sentence.getCoordinatedPhrases());
            // TODO Afegir comprovació de frase massa llarga i crear una nova
            //  entrada a l'Stack
        }
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

    public void addCoordinateSentence(Sentence sentence, boolean branch) {
        if (sentence.isVoid()) return;

        String realizedSentence = sentence.paragraphToString();
        if (Character.isUpperCase(realizedSentence.charAt(0))) {
            realizedSentence = realizedSentence.toLowerCase();
        }
        if (branch) {
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

    public String paragraphToString() {
        return realizeSentence(this.getParagraph()).replaceAll("[\\n]+[.]", ".\n").replace("\n, ", ", ").replaceAll("[\\n]+[ ]", "\n");
    }

    public void setCoordinatedPhrase(NLGElement phrase) {
        if (realizeSentence(phrase).replace("\n", "").equals("")) return;

        CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
        coordinatedPhrase.setConjunction(conjunction);
        coordinatedPhrases.removeAllElements();
        coordinatedPhrases.push(coordinatedPhrase);
        addCoordinateSentence(phrase);
    }

    public String firstSentenceToString() {
        return realizeSentence(coordinatedPhrases.get(0));
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

    public boolean isFinal() {
        return joinedVertex.get(joinedVertex.size() - 1).isFinal();
    }
}
