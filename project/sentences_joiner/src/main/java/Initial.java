import simplenlg.framework.NLGElement;

import java.util.Collection;

public class Initial extends Connector {
    public Initial() {
        super(java.util.List.of("Once ", "First of all ", "When "));
    }

    public Initial(String connector) {
        super(connector);
    }

    public Initial(Collection<String> connectors) {
        super(connectors);
    }

    @Override
    public void transformSentenceWithConnector(Sentence sentence) {
        String sentenceString = sentence.paragraphToString();
        sentenceString = selectedConnector + sentenceString;
        NLGElement branchPhrase = nlgFactory.createSentence(sentenceString);
        sentence.setCoordinatedPhrase(branchPhrase);
    }
}
