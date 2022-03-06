import org.json.simple.JSONObject;
import simplenlg.framework.NLGElement;

import java.util.ArrayList;

public class SentencesJoiner {
    JSONObject jsonElements;
    ArrayList<NLGElement> finalPhrases;

    String joinedSentences;

    public SentencesJoiner(JSONObject jsonElements, ArrayList<NLGElement> finalPhrases) {
        this.jsonElements = jsonElements;
        this.finalPhrases = finalPhrases;
    }
}
