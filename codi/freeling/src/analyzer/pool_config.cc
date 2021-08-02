

#include "freeling/morfo/util.h"

#include "logger.h"
#include "pool_config.h"

using namespace std;


pool_config::pool_config() : freeling::analyzer_config() {  

  // add our specific options to those of freeling::analyzer_config
  cf_opts.add_options()
    ("TagsetFile",po::wvalue<std::wstring>(&tagsetFile),"Tagset definition for PoS tag conversions");

  // set  ad-hoc value for kbest (freeling default is 1) 
  config_opt.TAGGER_kbest = KBEST;
}


//////////////////////////////////////////////////////////////
///  Destructor
//////////////////////////////////////////////////////////////

pool_config::~pool_config() {}


void pool_config::parse_options(const wstring &cfgFile) {
  // call freeling::analyzer_config to parse config file
  analyzer_config::parse_options(cfgFile);
  // deal with our added options
  tagsetFile = freeling::util::expand_filename(tagsetFile);
}


//////////////////////////////////////////////////////////////
/// Extract invoke options in request from given map, and
/// store them in a invoke_options struct
//////////////////////////////////////////////////////////////

freeling::analyzer_config::invoke_options pool_config::extract_invoke_options(const map<string,string> &params) const {

  // get default invoke options for current language
  freeling::analyzer_config::invoke_options ivk = invoke_opt;

  // overwrite needed options with requests contents
  for (map<string,string>::const_iterator op=params.begin(); op!=params.end(); ++op) {
    // assign value to appropriate option
    wistringstream sin(freeling::util::string2wstring(op->second));
    if (op->first=="OutputLevel")              sin >> ivk.OutputLevel;
    else if (op->first=="SenseAnnotation")     sin >> ivk.SENSE_WSD_which;
    else if (op->first=="Tagger")              sin >> ivk.TAGGER_which;
    else if (op->first=="DependencyParser")    sin >> ivk.DEP_which;
    else if (op->first=="SRLParser")           sin >> ivk.SRL_which;
    else if (op->first=="MultiwordDetection")  ivk.MACO_MultiwordsDetection = freeling::read_bool(sin);
    else if (op->first=="NumbersDetection")    ivk.MACO_NumbersDetection = freeling::read_bool(sin);
    else if (op->first=="DatesDetection")      ivk.MACO_DatesDetection = freeling::read_bool(sin);
    else if (op->first=="QuantitiesDetection") ivk.MACO_QuantitiesDetection = freeling::read_bool(sin);
    else if (op->first=="CompoundAnalysis")    ivk.MACO_CompoundAnalysis = freeling::read_bool(sin);
    else if (op->first=="NERecognition")       ivk.MACO_NERecognition = freeling::read_bool(sin);
    else if (op->first=="NEClassification")    ivk.NEC_NEClassification = freeling::read_bool(sin);
    else if (op->first=="Phonetics")           ivk.PHON_Phonetics = freeling::read_bool(sin);
  }

  //TS_LOG("AFTER:\n"<<freeling::util::wstring2string(ivk.dump()));
  return ivk;
}


