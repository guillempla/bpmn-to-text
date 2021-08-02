
#ifndef _TS_UTIL
#define _TS_UTIL

#include <string>

namespace util {

  /// split a (w)string into a container (set, list, vector...)
  /// C : container type (e.g. set<string>, vector<wstring>...)
  /// T: element type (string, wstring)
  template<class C, class T>
  inline C string_to(const T &ws, const T &sep) {
    C ls;
    if (ws.empty()) return ls;
    
    // at each occurence of separator "sep" in string "s", cut and insert at the end of the container
    size_t step = sep.size();
    size_t p=0;
    while (p != T::npos) {
      size_t q = ws.find(sep,p);
      T x = ws.substr(p,q-p);
      ls.insert(ls.end(),x);
      
      p = (q==T::npos ? q : q+step);
    }
    return(ls);
  }

}

#endif
