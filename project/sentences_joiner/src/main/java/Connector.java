import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Connector {
    protected final String separator = "-";
    protected String selectedConnector;
    protected ArrayList<String> connectors;
    protected final NLGFactory nlgFactory;
    protected final boolean randomConnector;

    public Connector() {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);

        randomConnector = false;
        selectConnector();
    }

    private void selectConnector() {
        if (!randomConnector) {
            selectConnector(0);
        }
        else {
            selectRandomConnector();
        }
    }

    public Connector(String connector) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);

        connectors.add(connector);
        randomConnector = false;
        selectConnector();
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

    public Connector(Collection<String> connectors) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);

        this.connectors.addAll(connectors);
        randomConnector = false;
        selectConnector();
    }
}
