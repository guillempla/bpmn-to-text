import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jbpt.hypergraph.abs.Vertex;
import simplenlg.framework.NLGElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ElementVertex extends Vertex {
    // Inspired in https://github.com/pawelgalka/pm_split_miner/blob/5c140f1d31303a5afc0337c481c80568f10d6416/java-joiner/src/main/java/pl/edu/agh/Graph.java
    private final String sentence;
    private final NLGElement phrase;
    private final String type;
    private boolean added;
    private final Map<String, Pair<String, Boolean>> next;

    public ElementVertex(String id, String sentence, NLGElement phrase, String type, ArrayList<Pair<String, String>> next) {
        super(id);
        this.sentence = sentence;
        this.phrase = phrase;
        this.type = type;
        this.added = false;
        this.next = convertNext(next);
    }

    private Map<String, Pair<String, Boolean>> convertNext(ArrayList<Pair<String, String>> next) {
        Map<String, Pair<String, Boolean>> nextAuxiliar = new HashMap<>();
        for (Pair<String, String> pair : next) {
            Pair<String, Boolean> auxPair = new ImmutablePair<>(pair.getValue(), false);
            nextAuxiliar.put(pair.getKey(), auxPair);
        }
        return nextAuxiliar;
    }

    public Map<String, Pair<String, Boolean>> getNext() {
        return this.next;
    }

    public boolean getNextVisited(String nextId) {
        return next.get(nextId).getValue();
    }

    public ArrayList<String> getNextIds() {
        return new ArrayList<>(next.keySet());
    }

    public ArrayList<String> getNextNames() {
        ArrayList<String> nextNames = new ArrayList<>();
        for (Pair<String, Boolean> pair : next.values()) {
            nextNames.add(pair.getKey());
        }
        return nextNames;
    }

    public Boolean isOpenGateway() {
        return isGateway() && next.size() > 1;
    }

    public Boolean isGateway() {
        return this.type.toLowerCase().contains("gateway");
    }

    public Boolean isBifurcation() {
        return next.size() > 1;
    }

    public Boolean isAdded() {
        return this.added;
    }

    public String getElementId() {
        return super.getName();
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

    public void setAdded(Boolean visited) {
        this.added = visited;
    }

    public void setNextVisited(String nextId, Boolean visited) {
        String name = this.next.get(nextId).getKey();
        this.next.put(nextId, new ImmutablePair<>(name, visited));
    }

    public boolean isInitial() {
        return this.type.toLowerCase().contains("startevent");
    }
}
