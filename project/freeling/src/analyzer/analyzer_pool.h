

#ifndef _ANALYZER_POOL
#define _ANALYZER_POOL

#include "worker.h"
#include "pool_config.h"
#include "actionSRL.h"

#include "freeling/morfo/analyzer.h"
#include "freeling/output/output.h"

/////////////////////////////////////////////////////////
///
///  Class analyzer_pool provides the intfrastructure to
///  create FreeLing-based service for TextServer
///
/////////////////////////////////////////////////////////

class analyzer_pool : public worker {

  protected:
    /// configuration files for each language
    std::map<std::string,std::wstring> anlz_config;
    /// configuration files for each output format
    std::map<std::string,std::wstring> out_config ;    

    /// analyzers for each loaded language
    std::map<std::string,freeling::analyzer*> anlz;
    /// output handlers for each loaded language and output format (map key: lang+"#"+outf)
    std::map<std::string,freeling::io::output*> out;
    /// configuration for each loaded analyzer 
    std::map<std::string, pool_config*> config;
    /// tagset description for each language
    std::map<std::string, freeling::tagset*> tagst;

    static actionSRL action_extractor;
    static std::string toJSON(const std::list<std::pair<std::wstring,std::list<action>>> &acts);
  
  public:
    /// constructor
    analyzer_pool(const std::string &cfgfile);
    /// destructor
    ~analyzer_pool();

    /// load a new language analyzer and associated output handlers
    worker_status adapt_configuration(const std::map<std::string,std::string> &params);

    /// apply freeling analizer to given text, and appropriately format the output
    std::string process(const std::map<std::string,std::string> &params, int &nw);
};


#endif
