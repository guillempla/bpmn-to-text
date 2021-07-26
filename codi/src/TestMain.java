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

        // Define a noun phrase for 'Mary' and 'the monkey' and a verb phrase for 'chase'
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("Mary");
        //NPPhraseSpec subject2 = nlgFactory.createNounPhrase("your", "giraffe");
        NPPhraseSpec object1 = nlgFactory.createNounPhrase("the monkey");
        //NPPhraseSpec object2 = nlgFactory.createNounPhrase("George");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("chase");

        // Apply the adjective 'fast' to Mary
        //subject.addModifier("fast");
        // Add the adverb 'quickly' to the verb
        //verb.addModifier("quickly");

        // Define the components of the sentence we wish to construct
        SPhraseSpec p = nlgFactory.createClause();

        //CoordinatedPhraseElement subj = nlgFactory.createCoordinatedPhrase(subject1, subject2);
        // // may revert to nlgFactory.createCoordinatedPhrase( subject1, subject2 ) ;
        //p.setSubject(subj);
        p.setSubject(subject1);

        //CoordinatedPhraseElement obj = nlgFactory.createCoordinatedPhrase(object1, object2);
        // // may revert to nlgFactory.createCoordinatedPhrase( subject1, subject2 ) ;
        //obj.addCoordinate("Martha");
        //obj.setFeature(Feature.CONJUNCTION, "or");
        //p.setObject(obj);
        p.setObject(object1);


        //p.setSubject("Mary");
        p.setVerb("chase");
        //p.setObject("the monkey");




        //p.setSubject(subject);
        //p.setObject(object);
        p.setVerb(verb);

        //p.setFeature(Feature.TENSE, Tense.PAST); // Set the phrase in the past
        //p.setFeature(Feature.TENSE, Tense.FUTURE); // Set the phrase in the future
        //p.setFeature(Feature.NEGATED, true); // Negate the phrase
        //p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO); // Ask simple yes/no questionS
        //p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT); // Ask about the object

        // Complements are anything that comes after a verb
        //p.addComplement("very quickly"); // Adverb phrase, passed as a string
        //p.addComplement("despite her exhaustion"); // Prepositional phrase, string


        // Combine the different components of the sentence
        //String output2 = realiser.realiseSentence(p); // Realiser created earlier.
        //System.out.println(output2);

        NPPhraseSpec place = nlgFactory.createNounPhrase("park");
        place.setDeterminer("the");
        place.addPreModifier("leafy");
        PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
        pp.addComplement(place);
        pp.setPreposition("in");

        p.addComplement(pp);

        String output3 = realiser.realiseSentence(p); // Realiser created earlier.
        System.out.println(output3);

        SPhraseSpec ss1 = nlgFactory.createClause("my cat", "like", "fish");
        SPhraseSpec ss2 = nlgFactory.createClause("my dog", "like", "big bones");
        SPhraseSpec ss3 = nlgFactory.createClause("my horse", "like", "grass");

        CoordinatedPhraseElement c = nlgFactory.createCoordinatedPhrase();
        c.setConjunction("but");
        c.addCoordinate(ss1);
        c.addCoordinate(ss2);
        c.addCoordinate(ss3);

        String output4 = realiser.realiseSentence(c);
        System.out.println(output4);

        SPhraseSpec p1 = nlgFactory.createClause("I", "be", "happy");
        SPhraseSpec q1 = nlgFactory.createClause("I", "eat", "fish");

        q1.setFeature(Feature.COMPLEMENTISER, "because");
        q1.setFeature(Feature.TENSE, Tense.PAST);
        p1.addComplement(q1);

        String output5 = realiser.realiseSentence(p1);
        System.out.println(output5);



    }

}