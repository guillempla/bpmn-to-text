import simplenlg.framework.NLGElement;

import java.util.Collection;

public class Branch extends Connector {
    public Branch() {
        super(java.util.List.of("If the answer is - then "));
    }

    public Branch(String connector) {
        super(connector);
    }

    public Branch(Collection<String> connectors) {
        super(connectors);
    }

    @Override
    public String transformStringWithConnector(String sentenceString, String name) {
        return selectedConnector.replace(separator, name) + sentenceString;
    }
}