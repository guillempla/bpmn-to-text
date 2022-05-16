import simplenlg.framework.NLGElement;

import java.util.Collection;

public class Bifurcation extends Connector {
    public Bifurcation() {
        super(java.util.List.of("the condition - is checked"));
    }

    public Bifurcation(String connector) {
        super(connector);
    }

    public Bifurcation(Collection<String> connectors) {
        super(connectors);
    }

    @Override
    public void transformSentenceWithConnector(Sentence sentence) {
        String sentenceString = sentence.paragraphToString();
        sentenceString = selectedConnector.replace(separator, sentenceString);
        NLGElement branchPhrase = nlgFactory.createSentence(sentenceString);
        sentence.setCoordinatedPhrase(branchPhrase);
    }
}