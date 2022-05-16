import simplenlg.framework.NLGElement;

import java.util.Collection;

public class Conjunction extends Connector {
    public Conjunction() {
        super(java.util.List.of("then", "after", "and", "later", "next", "after that"));
    }

    public Conjunction(String connector) {
        super(connector);
    }

    public Conjunction(Collection<String> connectors) {
        super(connectors);
    }

    public String transformSentenceWithConnector(Sentence sentence) {
        String sentenceString = sentence.paragraphToString();
        NLGElement branchPhrase = nlgFactory.createSentence(sentenceString);
        sentence.setConjunction(selectedConnector);
        sentence.setCoordinatedPhrase(branchPhrase);
        return sentenceString;
    }
}