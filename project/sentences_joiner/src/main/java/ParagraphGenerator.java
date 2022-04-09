import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.graph.abs.IFragment;
import org.jbpt.hypergraph.abs.Vertex;
import simplenlg.framework.NLGElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;
import java.util.Set;

public class ParagraphGenerator {
    private final RPST<DirectedEdge, Vertex> rpst;
    private final SentencesJoiner joiner;
    private final Realiser realiser;

    private NLGElement joinedSentences;

    public ParagraphGenerator(MultiDirectedGraph graph) {
        this.rpst = new RPST<>(graph);
        this.joiner = new SentencesJoiner();
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        realiser = new Realiser(lexicon);
        this.joinSentences();
    }

    public String getJoinedSentences() {
        return joiner.sentenceToString(this.joinedSentences);
    }

    private void joinSentences() {
        IRPSTNode<DirectedEdge, Vertex> root = rpst.getRoot();
        ElementVertex rootEntry = (ElementVertex) root.getEntry();
        rootEntry.setVisited(true);

        this.joinedSentences = traverseTree(root);
    }

    private NLGElement traverseTree(IRPSTNode<DirectedEdge, Vertex> node) {
        // Join entry sentence only if different of parent entry sentence
        ArrayList<NLGElement> childrenSentences = new ArrayList<>();
        ElementVertex entry = (ElementVertex) node.getEntry();
        if (!entry.isVisited()) {
            entry.setVisited(true);
            childrenSentences.add(entry.getPhrase());
        }

        // Base case
        if (isLeaf(node)) {
            ElementVertex exit = (ElementVertex) node.getExit();
            exit.setVisited(true);
            childrenSentences.add(exit.getPhrase());
            return joiner.joinSentences(exit, childrenSentences);
        }

        // Recursive case

        // Join entry sentence only if different of parent entry sentence
        ArrayList<NLGElement> childrenSentences = new ArrayList<>();
        Vertex parentEntry = getParentEntryVertex(node);
        if (parentEntry != node.getEntry() || parentEntry == null) {
            ElementVertex entry = (ElementVertex) node.getEntry();
            childrenSentences.add(entry.getPhrase());
        }

        /* If the node doesn't bifurcate, or it isn't a RIGID, we want to traverse the tree in a sorted way.
         * That means, handle the nodes that happen before in the BPMN.
         * If the node is a Gateway (or a RIGID) the order doesn't matter. */
        ElementVertex entry = (ElementVertex) node.getEntry();
        Set<IRPSTNode<DirectedEdge, Vertex>> nodesChildren = rpst.getChildren(node); // Children are unsorted
        Vertex currentVertex = node.getEntry();
        if (!nodeBifurcates(node, nodesChildren)) {
            while (nodesChildren.size() > 0) {
                ArrayList<IRPSTNode<DirectedEdge, Vertex>> children = findChildrenEqualToCurrentVertex(currentVertex, nodesChildren);

                IRPSTNode<DirectedEdge, Vertex> child = children.get(0); // if we are here we know that only one child is returned, so we can get the first (and only) one
                updateChildrenSentences(child, childrenSentences);

                currentVertex = child.getExit();
                nodesChildren.remove(child);
            }
        }
        else if (entry.isGate()) { // Read gateway children in the same order as
            ArrayList<String> nextId = entry.getNextIds();
            while (nextId.size() > 0) {
                String id = nextId.get(0);
                IRPSTNode<DirectedEdge, Vertex> child = findChildEqualId(id, nodesChildren);
                if (child != null) {
                    updateChildrenSentences(child, childrenSentences);
                    nodesChildren.remove(child);
                    nextId.remove(0);
                }
                else {
                    System.out.println("ERROR: Gate has no child with id: " + id);
                    System.out.println(entry.getNextIds());
                    printRPSTNode(node);
                    return null;
                }
            }
        }
        else { // RIGID
            for (IRPSTNode<DirectedEdge, Vertex> child : nodesChildren) {
                updateChildrenSentences(child, childrenSentences);
            }
        }

        NLGElement nlgElement = joiner.joinSentences(entry, childrenSentences);
//        System.out.println(realiser.realiseSentence(nlgElement));
        return nlgElement;
    }

    private void updateChildrenSentences(IRPSTNode<DirectedEdge, Vertex> child, ArrayList<NLGElement> childrenSentences) {
        ElementVertex entry = (ElementVertex) child.getEntry();
        entry.setVisited(true);
        NLGElement sentence = traverseTree(child);
        childrenSentences.forEach(childtest -> System.out.println(realiser.realiseSentence(childtest)));
        System.out.println();
        childrenSentences.add(sentence);
    }

    private boolean nodeBifurcates(IRPSTNode<DirectedEdge, Vertex> node, Set<IRPSTNode<DirectedEdge, Vertex>> nodesChildren) {
        ArrayList<IRPSTNode<DirectedEdge, Vertex>> children = findChildrenEqualToCurrentVertex(node.getEntry(), nodesChildren);
        return children.size() != 1 || node.getType() == TCType.RIGID;
    }

    private ArrayList<IRPSTNode<DirectedEdge, Vertex>> findChildrenEqualToCurrentVertex(Vertex currentVertex, Set<IRPSTNode<DirectedEdge, Vertex>> nodesChildren) {
        ArrayList<IRPSTNode<DirectedEdge, Vertex>> children = new ArrayList<>();
        for (IRPSTNode<DirectedEdge, Vertex> child : nodesChildren) {
            if (child.getEntry() == currentVertex) {
                children.add(child);
            }
        }
        return children;
    }

    private IRPSTNode<DirectedEdge, Vertex> findChildEqualId(String id, Set<IRPSTNode<DirectedEdge, Vertex>> nodesChildren) {
        for (IRPSTNode<DirectedEdge, Vertex> child : nodesChildren) {
            IFragment<DirectedEdge, Vertex> fragment = child.getFragment();
            for (DirectedEdge directedEdge : fragment) {
                if (directedEdge.getTarget().getName().equals(id)) {
                    return child;
                }
            }
        }

        return null;
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
        System.out.println(entry.getType());
        System.out.println(exit != null ? exit.getElementId() : "ExitNull");
//        System.out.println(entry.isVisited());
        System.out.println(node.getType());
        System.out.println(isLeaf(node) ? "LEAF" : "No Leaf");
    }
}
