
#ifndef _POOL_CONFIG
#define _POOL_CONFIG

#include "freeling/morfo/analyzer_config.h"

/////////////////////////////////////////////////////////
///
///  Class analyzer_config handles the FreeLing 
/// configuration for an analyzer
///
/////////////////////////////////////////////////////////
  
class pool_config : public freeling::analyzer_config {

 public:

    // number of PoS tagger sequences to check
    static const int KBEST = 3;

    /// specific options
    std::wstring tagsetFile;
    
    // constructor and destructor
    pool_config();
    ~pool_config();

    void parse_options(const std::wstring &cfgFile);

    // inherit other versions of parse_options
    using analyzer_config::parse_options;
    
    //void store_configuration(const po::variables_map &vm);
    freeling::analyzer_config::invoke_options extract_invoke_options(const std::map<std::string,std::string> &params) const;

};


#endif
