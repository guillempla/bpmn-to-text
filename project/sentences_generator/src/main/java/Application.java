import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;

public class Application {
    public static void main(String[] args) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser(lexicon);
    }
}
