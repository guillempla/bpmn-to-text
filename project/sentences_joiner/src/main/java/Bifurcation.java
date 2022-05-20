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
    public String transformStringWithConnector(String sentenceString) {
        return selectedConnector.replace(separator, sentenceString);
    }
}