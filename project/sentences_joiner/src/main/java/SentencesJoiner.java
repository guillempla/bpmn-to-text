import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;

import java.util.ArrayList;

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
        boolean notBranches = sentences.stream().anyMatch(Sentence::onlyOneGateway);
        return vertex.getNext().size() > 1 && sentences.size() > 1 && !notBranches;
    }

    private boolean vertexIsFirstGateway(ArrayList<Sentence> sentences) {
        return sentences.get(0).isFirstGateway();
    }

    private Sentence joinBranches(ElementVertex vertex, ArrayList<Sentence> sentences) {
        Sentence coordinatedSentence = new Sentence();

        addNameToBranches(vertex.getNextNames(), sentences);
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
            int totalWordCount = sentence.numWords() + coordinatedSentence.numWords();
            if (totalWordCount < 50) {
                coordinatedSentence.addCoordinateSentence(sentence);
            }
            else {
                // TODO Treatment for long sentences
                coordinatedSentence.addCoordinateSentence(sentence);
            }
        }
    }

    private void addNameToBranches(ArrayList<String> names, ArrayList<Sentence> sentences) {
        if (names.size() != sentences.size()) {
            System.out.println("ERROR: Names size different than sentences size");
            System.out.println("    " + names);
            System.out.println("    " + sentences);
            System.out.print("    [");
            sentences.forEach(Sentence::printSentence);
            System.out.println("]");
            return;
        }

        for (int i = 0; i < names.size(); i++) {
            String sentenceString = sentences.get(i).sentenceToString();
            sentenceString = "If the answer is " + names.get(i) + " then " + sentenceString;
            NLGElement branchPhrase = nlgFactory.createSentence(sentenceString);
            sentences.get(i).setCoordinatedPhrase(branchPhrase);
        }
    }

}
