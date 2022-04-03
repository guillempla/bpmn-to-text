import org.jbpt.algo.tree.tctree.TCType;

import java.util.ArrayList;

public class SentencesJoiner {
    private final String commaJoin = ", ";
    private final String dotJoin = ". ";

    public SentencesJoiner() {
    }

    public String joinSentences(TCType nodeType, ArrayList<String> sentences) {
        sentences.removeIf(sentence -> sentence.equals(""));
        StringBuilder joinedSentence = new StringBuilder(sentences.get(0));
        sentences.remove(0);
        for (String sentence : sentences) {
            int totalWordCount = countWords(sentence) + countWords(joinedSentence.toString());
            if (totalWordCount < 50) {
                joinedSentence.append(commaJoin).append(sentence);
            } else {
                joinedSentence.append(dotJoin).append(sentence);
            }
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

    private int countWords(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return 0;
        }

        String[] words = sentence.split("\\s+");
        return words.length;
    }
}
