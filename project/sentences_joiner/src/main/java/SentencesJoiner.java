import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

public class SentencesJoiner {
    private MultiDirectedGraph graph;
    private RPST<DirectedEdge, Vertex> rpst;

    private String joinedSentences;

    public SentencesJoiner(MultiDirectedGraph graph) {
        this.graph = graph;
        this.rpst = new RPST<>(graph);
        this.joinSentences();
    }

    public String getJoinedSentences() {
        return joinedSentences;
    }

    private void joinSentences() {
        IDirectedGraph<DirectedEdge, Vertex> g = rpst.getGraph();
        this.joinedSentences = "";
    }
}
