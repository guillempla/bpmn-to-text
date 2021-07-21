This folder contains SimpleNLG Version 4.5.0

For information on SimpleNLG, please see
https://github.com/simplenlg/simplenlg

-------------------------
Version notes for v 4.5.0

Major changes:
** Change of license version from MPL 1.0 to 2.0
** Removal of support for JVM 1.6 and 1.7.


Bug Fixes:
** Support exclamatory sentences: https://github.com/simplenlg/simplenlg/issues/34
** Appositives for noun phrase post modifier: https://github.com/simplenlg/simplenlg/issues/35
** SimpleNLG tests fail under Java 9: https://github.com/simplenlg/simplenlg/issues/54
** XmlRealiser verb morphology incorrect for perfect passive: https://github.com/simplenlg/simplenlg/issues/58
** SimpleNLG HTMLFormatter creates <h1> even when there is no document title: https://github.com/simplenlg/simplenlg/issues/65
** default-lexicon.xml lists "immune" as a colour: https://github.com/simplenlg/simplenlg/issues/75
** default-lexicon.xml uses verbModifier for "about": https://github.com/simplenlg/simplenlg/issues/76

-------------------------
Version notes for v 4.4.8

** SimpleNLG is now hosted on GitHub.
** Migration to Apache Maven for build process. 
** Support for using SimpleNLG as a maven artifact on OSSRH.
** JUnit test code clean up.
** New features:
	+ Support for parentheticals: https://github.com/simplenlg/simplenlg/issues/13
** Bug fixes:
	+ Fix for XMLLexicon entry being changed when the sentence is realised: https://github.com/simplenlg/simplenlg/issues/16
	+ Fix for double commas when two appositives are next to each other: https://github.com/simplenlg/simplenlg/issues/15
	+ Fix for possessive specifier of NP gets pluralized too when NP is pluralized: https://github.com/simplenlg/simplenlg/issues/9
	+ Fix for Method realise(List<NLGElement> elements) inside the Realiser.java is not implemented: https://github.com/simplenlg/simplenlg/issues/8
	
-------------------------
Version notes for v 4.4.3 

** Main new feature for this release is improved Text and HTML formatters. Both now support lists including multi-level enumerated lists. 
** Various bug fixes and general improvements.

-------------------------
Version notes for v 4.4.2.1 

** Updated version of 4.4.2 release with critical fix to prevent null pointer expections in the OrthographyProcessor.

-------------------------
Version notes for v 4.4.2

** Latest version of SimpleNLG compiled with all bug fixes since August 2012.

-------------------------
Version notes for V 4.4:

** Latest version of SimpleNLG compiled with all bug fixes since June 2011.
** Added HTMLFormatter. This allows SimpleNLG output to be formatted in HTML tags.

N.B:

simplenlg-v4.4.2 jar has been build for JVM's 1.6+ and higher.
