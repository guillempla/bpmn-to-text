import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
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

        printRPSTNode(root);
        System.out.println();

        this.joinedSentences = traverseTree(root);
        String[] separated = this.joinedSentences.split(",");
        System.out.println(separated.length);
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
        Set<IRPSTNode<DirectedEdge, Vertex>> nodesChildren = rpst.getChildren(node); // Children are unsorted
        nodesChildren.forEach(this::printRPSTNode);

        /* If the parent is not a Gateway or a Rigid. We want to traverse the tree in a sorted way.
         * That means, handle the nodes that happen before in the BPMN.
         * If the node is a Gateway the order doesn't matter. */
        Vertex currentVertex = node.getEntry();
        if (nodeBifucates(node)) {
            while (nodesChildren.size() > 0) {
                System.out.println("Children length: " + nodesChildren.size());
                ArrayList<IRPSTNode<DirectedEdge, Vertex>> children = getChildEqualToCurrentVertex(currentVertex, nodesChildren);

                IRPSTNode<DirectedEdge, Vertex> child = children.get(0);
                ElementVertex entry = (ElementVertex) child.getEntry();
                entry.setVisited(true);
                String sentence = traverseTree(child);
                childrenSentences.add(sentence);

                currentVertex = child.getExit();
                nodesChildren.remove(child);
            }
        } else {
            for (IRPSTNode<DirectedEdge, Vertex> child : nodesChildren) {
                ElementVertex entry = (ElementVertex) child.getEntry();
                entry.setVisited(true);
                String sentence = traverseTree(child);
                childrenSentences.add(sentence);
            }
        }

        return joiner.joinSentences(node.getType(), childrenSentences);
    }

    private boolean nodeBifucates(IRPSTNode<DirectedEdge, Vertex> node) {
        // TODO Check when an activity or task bifurcates.
        // TODO Segurament la forma més fàcil de comprovar-ho è amb la funció getChildEqualToCurrentVertex.
        // TODO Hauria de comprovar si la mida de children è diferent d'1.
        ElementVertex nodeEntry = (ElementVertex) node.getEntry();
        return !nodeEntry.isGate() && node.getType() != TCType.RIGID;
    }

    private ArrayList<IRPSTNode<DirectedEdge, Vertex>> getChildEqualToCurrentVertex(Vertex currentVertex, Set<IRPSTNode<DirectedEdge, Vertex>> nodesChildren) {
        // TODO Return arraylist with all possible children that have the same entry as **currentVertex**
        ArrayList<IRPSTNode<DirectedEdge, Vertex>> children = new ArrayList<>();
        for (IRPSTNode<DirectedEdge, Vertex> child : nodesChildren) {
            if (child.getEntry() == currentVertex) {
                children.add(child);
            }
        }
        return children;
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
        System.out.println(isLeaf(node) ? "LEAF" : "No Leaf");
    }
}
