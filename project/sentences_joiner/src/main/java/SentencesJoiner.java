import org.jbpt.algo.tree.tctree.TCType;

import java.util.ArrayList;

public class SentencesJoiner {
    private final String auxiliarJoin = ", ";

    public SentencesJoiner() {
    }

    public String joinSentences(TCType nodeType, ArrayList<String> sentences) {
        sentences.removeIf(sentence -> sentence.equals(""));
        StringBuilder joinedSentence = new StringBuilder(sentences.get(0));
        sentences.remove(0);
        for (String sentence : sentences) {
            joinedSentence.append(auxiliarJoin).append(sentence);
        }
        return joinedSentence.toString();
    }

//    public NLGElement joinSentences(TCType nodeType, ArrayList<NLGElement> sentences) {
//        NLGElement joinedSentence = null;
//        for (NLGElement sentence : sentences) {
//
//        }
//        return joinedSentence;
//    }

    private int countSentenceLenght(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return 0;
        }

        String[] words = sentence.split("\\s+");
        return words.length;
    }
}
