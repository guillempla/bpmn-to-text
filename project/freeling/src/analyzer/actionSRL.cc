
#include <iostream>
#include "logger.h"
#include "actionSRL.h"
#include "freeling/morfo/util.h"

using namespace std;
using namespace freeling;

/////////////////////////////////////////
/// class "action" methods 

wstring action::words(const list<word> &lw) const {
  wstring s;
  for (list<word>::const_iterator w=lw.begin(); w!=lw.end(); ++w)
    s += L" " + w->get_form();
  return (s.empty() ? s : s.substr(1));
}

wstring action::asString() const {
  wstring s = L"[pred=" + predH.get_form() + L" (" + words(pred) + L")";
  if (not obj.empty()) s += L", obj=" + objH.get_form() + L" (" + words(obj) + L")";
  if (not comp.empty()) s += L", comp=" + compH.get_form() + L" (" + words(comp) + L")";
  if (not other.empty()) s += L", other=" + otherH.get_form() + L" (" + words(other) + L")";
  s += L"]";
  return s;
}

string action::asJSON() const {
  string s = string("{") +
    "\"predW\" : \"" + util::wstring2string(words(pred)) + "\", " +
    "\"predF\" : \"" + util::wstring2string(predH.get_form()) + "\", " +
    "\"predL\" : \"" + util::wstring2string(predH.get_lemma()) + "\", " +
    "\"predPoS\" : \"" + util::wstring2string(predH.get_tag()) + "\", " +
    "\"predMSD\" : \"" + util::wstring2string(predH.user[0]) + "\"";
    // MSD info is added by analyzer_pool in user[0]
  
  if (not obj.empty()) {
    s += string(", ") +
      "\"objW\" : \"" + util::wstring2string(words(obj)) + "\", " +
      "\"objF\" : \"" + util::wstring2string(objH.get_form()) + "\", " +
      "\"objL\" : \"" + util::wstring2string(objH.get_lemma()) + "\", " +
      "\"objPoS\" : \"" + util::wstring2string(objH.get_tag()) + "\", " +
      "\"objMSD\" : \"" + util::wstring2string(objH.user[0]) + "\"";
  }
  if (not comp.empty()) {
    s += string(", ") +
      "\"compW\" : \"" + util::wstring2string(words(comp)) + "\", " +
      "\"compF\" : \"" + util::wstring2string(compH.get_form()) + "\", " +
      "\"compL\" : \"" + util::wstring2string(compH.get_lemma()) + "\", " +
      "\"compPoS\" : \"" + util::wstring2string(compH.get_tag()) + "\", " +
      "\"compMSD\" : \"" + util::wstring2string(compH.user[0]) + "\"";
  }
  if (not other.empty()) {
    s += string(", ") +
      "\"otherW\" : \"" + util::wstring2string(words(other)) + "\", " +
      "\"otherF\" : \"" + util::wstring2string(otherH.get_form()) + "\", " +
      "\"otherL\" : \"" + util::wstring2string(otherH.get_lemma()) + "\", " +
      "\"otherPoS\" : \"" + util::wstring2string(otherH.get_tag()) + "\", " + 
      "\"otherMSD\" : \"" + util::wstring2string(otherH.user[0]) + "\"";
  }
  
  s += "}";
  return s;
}

/////////////////////////////////////////
/// class "actionSRL" methods


actionSRL::actionSRL() {};
actionSRL::~actionSRL() {};

list<action> actionSRL::extract_actions(const sentence &s) const {

  list<action> sent_acts;

  bool found = false;
  // for each of the k best sequences proposed by the tagger
  for (int k=0; k<s.num_kbest() and not found; k++) {
    wstring lab = s.get_parse_tree(k).begin()->get_label();
    if (lab == L"action" or lab == L"noun-action" or lab == L"double-action") {
      TS_DEBUG("Found match at best sequence #" << k);
      wstring x = L" [";
      for (sentence::const_iterator w=s.begin(); w!=s.end(); ++w) 
        x += L" " + w->get_form() + L"/" + w->get_tag(k);
      x += L" ]";
      TS_DEBUG(freeling::util::wstring2string(x));
      TS_DEBUG(freeling::util::wstring2string(PrintTree(s,k)));

      found = true;
      
      const parse_tree &t = s.get_parse_tree(k);
      if (lab == L"action") sent_acts.push_back(ExtractAction(t.begin(),s));
      else if (lab == L"noun-action") sent_acts.push_back(ExtractNounAction(t.begin(),s));
      else if (lab == L"double-action") sent_acts = ExtractDoubleAction(t.begin(),s);
      
      for (list<action>::const_iterator a=sent_acts.begin(); a!=sent_acts.end(); ++a) {
        TS_DEBUG(freeling::util::wstring2string(a->asString()));
      }
      
    }
  }
  
  if (not found) {    
    TS_DEBUG("No match found. Available trees were:");
    for (int k=0; k<s.num_kbest(); k++) {
      TS_DEBUG("------- Tree #" << k);
      wstring x = L" [";
      for (sentence::const_iterator w=s.begin(); w!=s.end(); ++w) 
        x += L" " + w->get_form() + L"/" + w->get_tag(k);
      x += L" ]";
      TS_DEBUG(freeling::util::wstring2string(x));
      TS_DEBUG(freeling::util::wstring2string(PrintTree(s,k)));
    }
  }
  TS_DEBUG("");    

  return sent_acts;
}


//---------------------------------------------
// print syntactic tree
//---------------------------------------------

wstring actionSRL::rec_PrintTree(parse_tree::const_iterator n, int k, int depth) const {
  parse_tree::const_sibling_iterator d;
  
  wstring s = wstring(depth*3,' '); 
  if (n.num_children()==0) { 
    const word & w=n->get_word();
    s += wstring(n->is_head()? L"+" : L"") + L"(" + w.get_form() + L" " + w.get_lemma(k) + L" " + w.get_tag(k) + L")\n";
  }
  else { 
    s += (n->is_head()? L"+" : L"") + n->get_label() + L"_[\n";
    for (d=n.sibling_begin(); d!=n.sibling_end(); d++)
      s += rec_PrintTree(d, k, depth+1);

    s += wstring(depth*3,' ') + L"]\n";
  }

  return s;
}

// - - - - - - - - - - - - - - - - - - - - - - -
wstring actionSRL::PrintTree(const sentence &s, int k) const {
  return rec_PrintTree(s.get_parse_tree(k).begin(), k, 0);
}


// - - - - - - - - - - - - - - - - - - - - - - -
action actionSRL::ExtractAction(const parse_tree::const_iterator &t, const sentence &s) const {

  /// -----------  Extract action components:
  ///       verb-phrase -> predicate
  ///       noun-phrase -> object
  ///       *-prep-phrase -> complement
  ///       other -> other

  action act;
  for (parse_tree::const_sibling_iterator d=t.sibling_begin(); d!=t.sibling_end(); d++) {
    
    if (d->get_label()==L"verb-phrase") {
      act.predH = parse_tree::get_head_word(d);
      act.pred = get_words(d,s);
    }
    else if (d->get_label()==L"noun-phrase") {
      act.objH = parse_tree::get_head_word(d);
      act.obj = get_words(d,s);
    }
    else if (d->get_label()==L"n-prep-phrase" or d->get_label()==L"v-prep-phrase" ) {
      // a prep-phrase is always a preposition + noun-phrase.  Get the head from the noun-phrase    
      parse_tree::const_iterator c = d.nth_child(1);
      act.compH = parse_tree::get_head_word(c);
      act.comp = get_words(d,s);
    }
    else if (d->get_label()==L"other") {
      act.otherH = parse_tree::get_head_word(d);
      act.other = get_words(d,s);
    }
    else if (d->get_label()!=L"Fp") {
      // unexpected node under action non-terminal. Should not happen
      TS_WARNING("Skipping unexpected node " << freeling::util::wstring2string(d->get_label()));
    }
  }

  return act;
}


// - - - - - - - - - - - - - - - - - - - - - - -
action actionSRL::ExtractNounAction(const parse_tree::const_iterator &t, const sentence &s) const {
  
  /// -----------  Extract noun-action components:
  ///       noun-phrase:   Head -> predicate, rest->object
  ///       noun-phrase + prep-phrase(of) :  noun-phrase -> predicate, prep-phrase -> object 
  ///       noun-phrase + prep-phrase(*) :  noun-phrase -> predicate, prep-phrase -> complement 

  action act;

  // only one child, it is a single noun phrase, with no prepositional phrases.
  // --> the non-head words are the object e.g. "data transmission"
  if (t.num_children()==1) {
    parse_tree::const_iterator c = t.nth_child(0); // get noun-phrase
    for (parse_tree::const_sibling_iterator n = c.sibling_begin(); n!=c.sibling_end(); ++n) {
      if (n->is_head())
        act.predH = n->get_word();
      else {
        act.obj.push_back(n->get_word());
        act.objH = n->get_word(); // get last non-head word as complement head
      }
    }
  }
  
  // more than one child, it is a noun phrase + prepositional phrase "transmission of data"
  else {
    for (parse_tree::const_sibling_iterator d=t.sibling_begin(); d!=t.sibling_end(); d++) {
      
      if (d->get_label()==L"noun-action-phrase") {
        act.predH = parse_tree::get_head_word(d);
        act.pred = get_words(d,s);
      }
      else if (d->get_label()==L"n-prep-phrase") {
        // a prep-phrase is always a preposition + noun-phrase.  Get the head from the noun-phrase
        parse_tree::const_iterator c = d.nth_child(1);
        
        // get preposition
        wstring prep = d.nth_child(0)->get_word().get_lemma();
        
        if (prep == L"of") { // if preposition is "of", consider it the object ("transmission of data")
          act.objH = parse_tree::get_head_word(c);
          act.obj = get_words(d,s);
        }
        else { // other prepositions, consider it a complement ("transmission to the bank")
          act.compH = parse_tree::get_head_word(c);
          act.comp = get_words(d,s);
        }
      }
    }
  }

  return act;
}

// - - - - - - - - - - - - - - - - - - - - - - -
list<action> actionSRL::ExtractDoubleAction(const parse_tree::const_iterator &t, const sentence &s) const {

  list<action> acts;
  // more than one action child
  for (parse_tree::const_sibling_iterator d=t.sibling_begin(); d!=t.sibling_end(); d++) {
    if (d->get_label()==L"action") 
      acts.push_back(ExtractAction(d,s));
    else if (d->get_label()==L"noun-action") 
      acts.push_back(ExtractNounAction(d,s));
  }
  
  return acts;
}


// - - - - - - - - - - - - - - - - - - - - - - -
list<word> actionSRL::get_words(const parse_tree::const_iterator &d, const sentence &s) const {
  int from = parse_tree::get_leftmost_leaf(d)->get_word().get_position();
  int to = parse_tree::get_rightmost_leaf(d)->get_word().get_position();
  list<word> words;
  for (int i=from; i<=to; ++i)
    words.push_back(s[i]);
  return words;
}
