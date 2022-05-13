import java.util.ArrayList;
import java.util.Collection;

public abstract class Connector {
    protected final String separator = "-";
    protected String selectedConnector;
    protected ArrayList<String> connectors;

    public Connector(String connector) {
        connectors.add(connector);
    }

    public Connector(Collection<String> connectors) {
        this.connectors.addAll(connectors);
    }

    public String getSelectedConnector() {
        return selectedConnector;
    }

    public ArrayList<String> getConnectors() {
        return connectors;
    }

    protected void addConnectors(String connector) {
        connectors.add(connector);
    }

    protected void addConnectors(Collection<String> connectors) {
        this.connectors.addAll(connectors);
    }

    protected void selectRandomConnector() {

    }

    protected void selectConnector(int index) {
        selectedConnector = connectors.get(index);
    }

    protected String[] splitConnector(String connector) {
        return connector.split(separator);
    }

    abstract public void transformSentenceWithConnector(Sentence sentence);
}
