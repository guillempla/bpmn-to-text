import simplenlg.framework.NLGElement;

import java.util.Collection;

public class Final extends Connector {
    public Final() {
        super(java.util.List.of("Finally ", "To conclude ", "And finally ", "Lastly ", "The process finishes when "));
    }

    public Final(String connector) {
        super(connector);
    }

    public Final(Collection<String> connectors) {
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
