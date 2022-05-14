import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Connector {
    protected final String separator = "-";
    protected String selectedConnector;
    protected ArrayList<String> connectors;
    protected final NLGFactory nlgFactory;

    public Connector() {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);
    }

    public Connector(String connector) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);

        connectors.add(connector);
    }

    public Connector(Collection<String> connectors) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);

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
