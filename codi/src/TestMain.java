import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;


public class TestMain {

    public static void main(String[] args) {
        // Get information about words
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        // Create an object which creates simplenlg structures
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        // Create object which transforms simplenlg structures into text
        Realiser realiser = new Realiser(lexicon);

        NLGElement s1 = nlgFactory.createSentence("my dog is happy");

        String output = realiser.realiseSentence(s1);
        System.out.println(output);

        // Define the components of the sentence we wish to construct
        SPhraseSpec p = nlgFactory.createClause();
        p.setSubject("Mary");
        p.setVerb("chase");
        p.setObject("the monkey");
        //p.setFeature(Feature.TENSE, Tense.PAST); // Set the phrase in the past
        //p.setFeature(Feature.TENSE, Tense.FUTURE); // Set the phrase in the future
        //p.setFeature(Feature.NEGATED, true); // Negate the phrase
        //p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO); // Ask simple yes/no questionS
        //p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT); // Ask about the object

        // Complements are anything that comes after a verb
        p.addComplement("very quickly"); // Adverb phrase, passed as a string
        p.addComplement("despite her exhaustion"); // Prepositional phrase, string


        // Combine the different components of the sentence
        String output2 = realiser.realiseSentence(p); // Realiser created earlier.
        System.out.println(output2);



    }

}