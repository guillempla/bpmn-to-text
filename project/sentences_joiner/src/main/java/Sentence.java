import simplenlg.framework.NLGElement;

import java.util.ArrayList;

public class Sentence {
    private NLGElement phrase;
    private boolean isFirstGateway;
    private ArrayList<ElementVertex> joinedVertex;

    public Sentence(NLGElement phrase) {
        this.phrase = phrase;
        this.isFirstGateway = false;
        this.joinedVertex = new ArrayList<>();
    }

    public NLGElement getPhrase() {
        return phrase;
    }

    public void setPhrase(NLGElement phrase) {
        this.phrase = phrase;
    }

    public boolean isFirstGateway() {
        return isFirstGateway;
    }

    public void setFirstGateway(boolean firstGateway) {
        isFirstGateway = firstGateway;
    }

    public ArrayList<ElementVertex> getJoinedVertex() {
        return joinedVertex;
    }

    public void setJoinedVertex(ArrayList<ElementVertex> joinedVertex) {
        this.joinedVertex = joinedVertex;
    }
}
