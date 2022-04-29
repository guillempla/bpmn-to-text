import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.graph.abs.IFragment;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.ArrayList;
import java.util.Set;

public class ParagraphGenerator {
    private final RPST<DirectedEdge, Vertex> rpst;
    private final SentencesJoiner joiner;

    private Sentence joinedSentences;

    public ParagraphGenerator(MultiDirectedGraph graph) {
        this.rpst = new RPST<>(graph);
        this.joiner = new SentencesJoiner();
        this.joinSentences();
    }

    public String getJoinedSentences() {
        return this.joinedSentences.sentenceToString();
    }

    private void joinSentences() {
        IRPSTNode<DirectedEdge, Vertex> root = rpst.getRoot();
        this.joinedSentences = traverseTree(root);
    }

    private Sentence traverseTree(IRPSTNode<DirectedEdge, Vertex> node) {
        if (node.getType() == TCType.RIGID) {
            System.out.println("RIGID");
            System.out.println();
        }

        // Join entry sentence only if different of parent entry sentence
        ArrayList<Sentence> childrenSentences = new ArrayList<>();
        ElementVertex entry = (ElementVertex) node.getEntry();
        if (!entry.isAdded()) {
            entry.setAdded(true);
            Sentence sentence = new Sentence(entry.getPhrase(), entry);
            childrenSentences.add(sentence);
        }

        // Base case
        if (isLeaf(node)) {
            ElementVertex exit = (ElementVertex) node.getExit();
            exit.setAdded(true);
            Sentence sentence = new Sentence(exit.getPhrase(), exit);
            childrenSentences.add(sentence);
            return joiner.joinSentences(exit, childrenSentences);
        }

        // Recursive case

        /* If the node doesn't bifurcate, or it isn't a RIGID, we want to traverse the tree in a sorted way.
         * That means, handle the nodes that happen before in the BPMN.
         * If the node is a Gateway (or a RIGID) the order doesn't matter. */
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
        else if (entry.isOpenGateway()) { // Read gateway children in the same order as
            ArrayList<String> nextId = entry.getNextIds();
            while (nextId.size() > 0) {
                String id = nextId.get(0);
                if (entry.getNextVisited(id)) {
                    // if nextId has been treated skip it
                    nextId.remove(0);
                    break;
                }


                IRPSTNode<DirectedEdge, Vertex> child = findChildEqualId(id, nodesChildren);
                int it = 0;
                while (child == null) {
                    id = nextId.get(it);
                    child = findChildEqualId(id, nodesChildren);
                    if (++it >= nextId.size()) break;
                }
                if (child != null) {
                    updateChildrenSentences(child, childrenSentences);
                    entry.setNextVisited(id, true);
                    nextId.remove(0);
                }
                else {
                    // TODO fix this error
                    System.out.println("ERROR: Gate has no child with id: " + id);
                    System.out.println("    " + entry.getNextIds());
                    nodesChildren.forEach(nodeTest -> System.out.println("    " + nodeTest.getName()));
                    System.out.println();
                    printRPSTNode(node);

                    nextId.remove(0);
                }
            }
        }
        else { // RIGID
            for (IRPSTNode<DirectedEdge, Vertex> child : nodesChildren) {
                updateChildrenSentences(child, childrenSentences);
            }
        }

        return joiner.joinSentences(entry, childrenSentences);
    }

    private void updateChildrenSentences(IRPSTNode<DirectedEdge, Vertex> child, ArrayList<Sentence> childrenSentences) {
        ElementVertex entry = (ElementVertex) child.getEntry();
        Sentence sentence = traverseTree(child);
//        if (!childrenSentences.contains(null)) childrenSentences.forEach(Sentence::printlnSentence);
//        else System.out.println("WARNING: Children Sentences contain NULL!");
//        System.out.println();
        entry.setAdded(true);
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

    private boolean isLeaf(IRPSTNode<DirectedEdge, Vertex> node) {
        return rpst.getChildren(node).size() == 0;
    }

    private void printRPSTNode(IRPSTNode<DirectedEdge, Vertex> node) {
        ElementVertex entry = (ElementVertex) node.getEntry();
        ElementVertex exit = (ElementVertex) node.getExit();
        System.out.println("    " + entry.getElementId());
        System.out.println("    " + entry.getType());
        System.out.println(exit != null ? "    " + exit.getElementId() : "    ExitNull");
//        System.out.println("    " + entry.isVisited());
        System.out.println("    " + node.getType());
        System.out.println(isLeaf(node) ? "    LEAF" : "    No Leaf");
    }
}
