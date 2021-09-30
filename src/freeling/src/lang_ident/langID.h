#ifndef _LANGID
#define _LANGID

#include "worker.h"
#include "freeling/morfo/lang_ident.h"

/////////////////////////////////////////////////////////
///
///  Class lang_ident provides the intfrastructure to
///  create FreeLing lang_ident based service for TextServer
///
/////////////////////////////////////////////////////////

class langID : public worker {

  private:
    freeling::lang_ident *ident;

  public:
    /// constructor
    langID(const std::string &cfgdir);
    /// destructor
    ~langID();

    /// apply lang ident analizer to given text, and approprately format the output
    std::string process(const std::map<std::string,std::string> &params, int &nw);
  };

#endif
