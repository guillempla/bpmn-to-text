import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.ArrayList;
import java.util.Set;

public class ParagraphGenerator {
    private final RPST<DirectedEdge, Vertex> rpst;
    private final SentencesJoiner joiner;

    private String joinedSentences;

    public ParagraphGenerator(MultiDirectedGraph graph) {
        this.rpst = new RPST<>(graph);
        this.joiner = new SentencesJoiner();
        this.joinSentences();
    }

    public String getJoinedSentences() {
        return joinedSentences;
    }

    private void joinSentences() {
        IRPSTNode<DirectedEdge, Vertex> root = rpst.getRoot();
        ElementVertex rootEntry = (ElementVertex) root.getEntry();
        rootEntry.setVisited(true);

        this.joinedSentences = traverseTree(root);
        String[] seprarated = this.joinedSentences.split(",");
        System.out.println(seprarated.length);
    }

    private String traverseTree(IRPSTNode<DirectedEdge, Vertex> node) {
        // Base case
        if (isLeaf(node)) {
            ElementVertex exit = (ElementVertex) node.getExit();
            return (exit.getSentence() == null) ? "" : exit.getSentence();
        }

        // Recursive case

        // Join entry sentence only if different of parent entry sentence
        ArrayList<String> childrenSentences = new ArrayList<>();
        Vertex parentEntry = getParentEntryVertex(node);
        if (parentEntry != node.getEntry() || parentEntry == null) {
            ElementVertex entry = (ElementVertex) node.getEntry();
            String sentence = entry.getSentence() == null ? "" : entry.getSentence();
            childrenSentences.add(sentence);
        }

        Set<IRPSTNode<DirectedEdge, Vertex>> nodesChildren = rpst.getChildren(node);
        for (IRPSTNode<DirectedEdge, Vertex> rpstNode : nodesChildren) {
            ElementVertex entry = (ElementVertex) rpstNode.getEntry();
            entry.setVisited(true);
            String sentence = traverseTree(rpstNode);
            childrenSentences.add(sentence);
        }

        return joiner.joinSentences(node.getType(), childrenSentences);
    }

    private Vertex getParentEntryVertex(IRPSTNode<DirectedEdge, Vertex> node) {
        IRPSTNode<DirectedEdge, Vertex> parent = rpst.getParent(node);
        if (parent == null) {
            return null;
        }
        return parent.getEntry();
    }

    private boolean isLeaf(IRPSTNode<DirectedEdge, Vertex> node) {
        return rpst.getChildren(node).size() == 0;
    }

    private void printRPSTNode(IRPSTNode<DirectedEdge, Vertex> node) {
        ElementVertex entry = (ElementVertex) node.getEntry();
        ElementVertex exit = (ElementVertex) node.getExit();
        System.out.println(entry.getElementId());
        System.out.println(exit != null ? exit.getElementId() : "ExitNull");
//        System.out.println(entry.isVisited());
        System.out.println(node.getType());
//        System.out.println(isLeaf(node) ? "LEAF" : "No Leaf");
    }
}
