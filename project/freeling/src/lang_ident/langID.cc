
#include <ctime>

#include "logger.h"
#include "util.h"
#include "langID.h"

#include "freeling/morfo/traces.h"
#include "freeling/morfo/util.h"
#include "freeling/morfo/lang_ident.h"

using namespace std;


/////////////////////////////////////////
/// constructor, load identifier
/////////////////////////////////////////

langID::langID(const std::string &cfgfile) {
  // init freeling
  freeling::util::init_locale();

  wstring fname = freeling::util::string2wstring(cfgfile);
  wifstream fs;
  freeling::util::open_utf8_file(fs,fname);
  if (fs.fail()) 
    TS_ERROR("Error opening config file " << cfgfile)

  wstring langID_FL_config;
  getline(fs,langID_FL_config);
  fs.close();

  // create language identifier
  ident = new freeling::lang_ident(langID_FL_config); 
}

/////////////////////////////////////////
/// destructor: free resources
/////////////////////////////////////////

langID::~langID() { delete ident; }

/////////////////////////////////////////
///  Perform language identification and format handling
/////////////////////////////////////////

string langID::process(const std::map<std::string,std::string> &params, int &nw) {
  
  wstring wtext = freeling::util::string2wstring(params.find("text")->second);

  // process text using FreeLing
  string answer = freeling::util::wstring2string(ident->identify_language(wtext));

  // compute word count
  wstringstream ss(wtext); wstring w; 
  nw = 0; while (ss>>w) ++nw;
  
  // the result of the process must be a string containing XML (or json, depending on
  // the configuration and available options) appropriate for the expected results.
  string format = params.find("output")->second;
  if (format=="xml")
    return "<language>"+answer+"</language>";
  else // json
    return "{\"language\" : \""+answer+"\"}";
  
}


