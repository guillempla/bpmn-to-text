#ifndef _ACTIONSRL_H
#define _ACTIONSRL_H

#include <list>
#include "freeling/morfo/language.h"


/// class to store a predicate and complements
class action {
  private:
     std::wstring words(const std::list<freeling::word> &lw) const;
 
  public:
     std::list<freeling::word> pred;
     freeling::word predH;
     std::list<freeling::word> obj;
     freeling::word objH;
     std::list<freeling::word> comp;
     freeling::word compH;
     std::list<freeling::word> other;
     freeling::word otherH;

     std::wstring asString() const;
     std::string asJSON() const;
};


class actionSRL {

 public:
    actionSRL();
    ~actionSRL();
  
    std::list<action> extract_actions(const freeling::sentence &s) const;
    std::wstring PrintTree(const freeling::sentence &s, int k) const;

 private:

    std::wstring rec_PrintTree(freeling::parse_tree::const_iterator n, int k, int depth) const;
    action ExtractAction(const freeling::parse_tree::const_iterator &t, const freeling::sentence &s) const;
    
    action ExtractNounAction(const freeling::parse_tree::const_iterator &t, const freeling::sentence &s) const;
    std::list<action> ExtractDoubleAction(const freeling::parse_tree::const_iterator &t, const freeling::sentence &s) const;
    std::list<freeling::word> get_words(const freeling::parse_tree::const_iterator &d, const freeling::sentence &s) const;
};

#endif
