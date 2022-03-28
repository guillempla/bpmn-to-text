import org.jbpt.hypergraph.abs.Vertex;
import simplenlg.framework.NLGElement;

public class ElementVertex extends Vertex {
    // Inspired in https://github.com/pawelgalka/pm_split_miner/blob/5c140f1d31303a5afc0337c481c80568f10d6416/java-joiner/src/main/java/pl/edu/agh/Graph.java
    private final String sentence;
    private final NLGElement phrase;
    private final String type;
    private boolean visited;

    public ElementVertex(String id, String sentence, NLGElement phrase, String type) {
        super(id);
        this.sentence = sentence;
        this.phrase = phrase;
        this.type = type;
        this.visited = false;
    }

    public Boolean isGate() {
        return this.type.toLowerCase().contains("gateway");
    }

    public Boolean isVisited() {
        return this.visited;
    }

    public String getSentence() {
        return this.sentence;
    }

    public NLGElement getPhrase() {
        return this.phrase;
    }

    public String getType() {
        return this.type;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }
}
