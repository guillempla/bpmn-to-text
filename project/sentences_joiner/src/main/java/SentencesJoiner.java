import org.apache.commons.lang3.tuple.Pair;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;

import java.util.ArrayList;
import java.util.Map;

public class SentencesJoiner {
    private final NLGFactory nlgFactory;

    public SentencesJoiner() {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);
    }

    public Sentence joinSentences(ElementVertex vertex, ArrayList<Sentence> sentences) {
        sentences.removeIf(this::sentenceIsVoid);
        if (sentences.size() == 0) return new Sentence();

        if (joiningBranches(vertex, sentences)) {
//            System.out.println();
//            System.out.println(sentences);
//            System.out.println(sentences.size());
//            System.out.println(vertexIsFirstGateway(sentences));
//            vertex.getNextNames().forEach(System.out::println);
//            sentences.forEach(Sentence::printSentence);
            return joinBranches(vertex, sentences);
        }

        if (vertexIsFirstGateway(sentences)) {
            return joinGateways(vertex, sentences);
        }

        return joinActivities(vertex.getType(), sentences);
    }

    private boolean sentenceIsVoid(Sentence sentence) {
        return sentence == null || sentence.isVoid();
    }

    private boolean joiningBranches(ElementVertex vertex, ArrayList<Sentence> sentences) {
        if (!vertex.isBifurcation()) return false;

        boolean joiningBranches = true;
        ArrayList<String> nextIds = vertex.getNextIds();

        for (String id : nextIds) {
            boolean coincidenceId = false;
            for (Sentence sentence : sentences) {
                if (id.equals(sentence.getIdOfFirstJoinedVertex())) {
                    coincidenceId = true;
                    break;
                }
            }
            if (!coincidenceId) {
                joiningBranches = false;
                break;
            }
        }

        return joiningBranches;
    }

    private boolean vertexIsFirstGateway(ArrayList<Sentence> sentences) {
        return sentences.get(0).isFirstGateway();
    }

    private Sentence joinBranches(ElementVertex vertex, ArrayList<Sentence> sentences) {
        Sentence coordinatedSentence = new Sentence();
//        System.out.println("VERTEX: " + vertex.getSentence());
//        System.out.println("VERTEX: " + vertex.isBifurcation());
        addNameToBranches(vertex.getNext(), sentences);
        addSentencesToCoordinate(sentences, coordinatedSentence);

        return coordinatedSentence;
    }

    private Sentence joinGateways(ElementVertex gateway, ArrayList<Sentence> sentences) {
        Sentence coordinatedSentence = new Sentence();

        String sentenceString = sentences.get(0).sentenceToString();
        sentenceString = "the condition " + sentenceString + " is checked";
        NLGElement firstPhrase = nlgFactory.createSentence(sentenceString);
        Sentence firstSentence = new Sentence(firstPhrase, gateway);
        coordinatedSentence.addCoordinateSentence(firstSentence);
        sentences.remove(0);

        addSentencesToCoordinate(sentences, coordinatedSentence);

        return coordinatedSentence;
    }

    private Sentence joinActivities(String type, ArrayList<Sentence> sentences) {
        Sentence coordinatedSentence = new Sentence();

        addSentencesToCoordinate(sentences, coordinatedSentence);

        return coordinatedSentence;
    }

    private void addSentencesToCoordinate(ArrayList<Sentence> sentences, Sentence coordinatedSentence) {
        for (Sentence sentence : sentences) {
            coordinatedSentence.addCoordinateSentence(sentence);
        }
    }

    private void addNameToBranches(Map<String, Pair<String, Boolean>> next, ArrayList<Sentence> sentences) {
        if (next.size() != sentences.size()) {
            System.out.println("ERROR: Names size different than sentences size");
            System.out.println("    " + next);
            System.out.println("    " + sentences);
            System.out.print("    [");
            sentences.forEach(Sentence::printSentence);
            System.out.println("]");
            return;
        }

        for (Sentence sentence : sentences) {
            String elementId = sentence.getIdOfFirstJoinedVertex();
            String name = next.get(elementId).getKey();
            String sentenceString = sentence.sentenceToString();
            sentenceString = "If the answer is " + name + " then " + sentenceString;
            NLGElement branchPhrase = nlgFactory.createSentence(sentenceString);
            sentence.setCoordinatedPhrase(branchPhrase);
        }
    }

}
