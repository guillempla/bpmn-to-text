import java.util.Collection;

public class Initial extends Connector {
    public Initial() {
        super(java.util.List.of("Once ", "First of all ", "When ", "The process starts when ", "Firstly "));
    }

    public Initial(String connector) {
        super(connector);
    }

    public Initial(Collection<String> connectors) {
        super(connectors);
    }

    @Override
    public String transformStringWithConnector(String sentenceString) {
        return selectedConnector + sentenceString;
    }
}
