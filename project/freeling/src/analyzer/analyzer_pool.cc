
#include <set>
#include <ctime>

#include "logger.h"
#include "util.h"
#include "analyzer_pool.h"

#include "freeling/morfo/traces.h"

using namespace std;


/// helper class to extract actions from parse trees
actionSRL analyzer_pool::action_extractor;


////////////////////////////////////////////
///  Constructor: create TS_service, store configdir
////////////////////////////////////////////

analyzer_pool::analyzer_pool(const string &cfgfile) {

  //freeling::traces::TraceLevel = 6;
  //freeling::traces::TraceModule= 0xFFFFFFFF;
  
  freeling::util::init_locale();
  
  wstring fname = freeling::util::string2wstring(cfgfile);
  wstring path = fname.substr(0, fname.find_last_of(L"/\\")+1);
  
  wifstream fs;
  freeling::util::open_utf8_file(fs,fname);
  if (fs.fail()) 
    TS_ERROR("Error opening config file " << cfgfile)

  // load pairs option-value in config file
  set<string> languages, output;
  wstring line;
  while (getline(fs,line)) {
    freeling::util::find_and_replace(line,L" ",L"");
    freeling::util::find_and_replace(line,L"\t",L"");
    if (line.empty() or line[0]==L'#') continue;

    vector<wstring> v = util::string_to<vector<wstring>,wstring>(line, L"=");

    string key = freeling::util::wstring2string(v[0]);
    string val = freeling::util::wstring2string(v[1]);

    if (key=="languages") 
      languages = util::string_to<set<string>,string>(val, ",");
    
    else if (key=="output") 
      output = util::string_to<set<string>,string>(val, ",");

    else {
      // key must be either a language or an output format.
      // store 'val' (file name) into the right map.

      wstring v = freeling::util::string2wstring(val);
      v = freeling::util::absolute(v,path);

      if (languages.find(key) != languages.end()) 
        anlz_config.insert(make_pair(key,v));
      
      else if (output.find(key) != output.end())
        out_config.insert(make_pair(key,v));

      else 
        TS_ERROR("Undeclared key '" << key << "' in config file " << cfgfile);
    }
  }

  fs.close();
}

////////////////////////////////////////////
///  Destructor: free FreeLing modules
////////////////////////////////////////////

analyzer_pool::~analyzer_pool() {
  // Cleanup, and free server instance
  for (map<string,freeling::analyzer*>::iterator a=anlz.begin(); a!=anlz.end(); a++) 
    delete a->second;
  for (map<string,freeling::tagset*>::iterator t=tagst.begin(); t!=tagst.end(); t++) 
    delete t->second;
  for (map<string,freeling::io::output*>::iterator o=out.begin(); o!=out.end(); o++) 
    delete o->second;
}


////////////////////////////////////////////
/// Load freeling modules for given language
////////////////////////////////////////////

worker_status analyzer_pool::adapt_configuration(const map<string,string> &params) {

  string lang = params.find("language")->second;

  // check for analyzer for required language
  map<string,freeling::analyzer*>::iterator a = anlz.find(lang);

  freeling::analyzer *anl;
  pool_config *cfg;

  if (a != anlz.end()) {
    // Analyzer already exists, retrieve configuration, and analyzer itself
    cfg = config[lang];
    anl = a->second;
  }
  
  else {
    // No available analyzer for this language, load it

    TS_LOG("Loading analyzer for '" << lang << "'");
    // load ConfigOptions and InvokeOptions from language config file
    cfg = new pool_config();
    cfg->parse_options(anlz_config[lang]);
    // store configuration for this language, for future requests
    config.insert(make_pair(lang,cfg));

    // create analyzer with given configuration
    anl = new freeling::analyzer(cfg->config_opt);
    // store analyzer for this language, for future requests
    anlz.insert(make_pair(lang,anl));
    // load and store tagset manager for given language
    freeling::tagset *tgs = new freeling::tagset(cfg->tagsetFile);
    tagst.insert(make_pair(lang,tgs));

    // keep map iterator for later
    TS_LOG("   analyzer loaded");
  
    // create and store all output handlers for this language
    for (auto of : out_config) {
      TS_LOG("   Loading output handler '"+of.first+"' for language '" << lang << "'");
      freeling::io::output *s = new freeling::io::output(freeling::util::string2wstring(of.second));
      s->load_tagset(cfg->tagsetFile);
      s->set_language(freeling::util::string2wstring(lang));
      
      string key = lang + "#" + of.first;
      out.insert(make_pair(key, s));
      TS_LOG("   Registered output handler "+key);
    }

    TS_LOG("Language "+lang+" loaded");
  }

  // analyzer is already available (either previously existing or just created).
  // adapt configuration to current request needs.

  // start from default options, and overwrite with given params
  freeling::analyzer_config::invoke_options ivk = cfg->extract_invoke_options(params);

  // check options are OK
  worker_status ws;
  freeling::analyzer_config::status st = cfg->check_invoke_options(ivk);
  if (st.stat == freeling::CFG_ERROR) {
    ws.status = WRK_ERROR;
    ws.reason = freeling::util::wstring2string(st.description);
    TS_WARNING(freeling::util::wstring2string(st.description));
    return ws;
  }

  if (st.stat == freeling::CFG_WARNING) 
    TS_WARNING(freeling::util::wstring2string(st.description));

  // set options for this request
  anl->set_current_invoke_options(ivk);

  ws.status = WRK_OK;
  return ws;
}




////////////////////////////////////////////
/// Apply freeling modules to given text, 
/// return TS-compliant XMl response
////////////////////////////////////////////

string analyzer_pool::process(const map<string,string> &params, int &nw) {

  TS_LOG("Processing request text");
  string lang = params.find("language")->second;
  string text = params.find("text")->second;
  string outf = params.find("output")->second;
  string outlv = params.find("OutputLevel")->second;

  // get analyzer for requested language (should have been loaded if needed)
  freeling::analyzer *an = anlz.find(lang)->second;
  
  string answer;
  if (outlv == "shallow") {
    // list of labels, one per line.
    wstring wtext = freeling::util::string2wstring(text);
    list<wstring> lines = freeling::util::wstring2list(wtext,L"\n");

    list<pair<wstring,list<action>>> acts;
    nw = 0;
    for (auto lin : lines) {
      if (lin==L"") continue;
      list<freeling::sentence> ls;

      // call analyzer to tokenize only
      auto ivk = anlz.find(lang)->second->get_current_invoke_options();
      ivk.OutputLevel = freeling::TOKEN;
      an->set_current_invoke_options(ivk);
      an->analyze(lin, ls, true);      

      // check tokenized words and lowercase all except acronyms
      for (auto &s : ls) {
        nw += s.size(); // count sentence words to please TS
        for (auto &w : s) {
          wstring form = w.get_form();
          if (freeling::util::capitalization(form)==UPPER_1ST) w.set_form(freeling::util::lowercase(form));
        }
      }

      // set analyzer to continue analysis from tokenized to shallow
      ivk.InputLevel = freeling::TOKEN;
      ivk.OutputLevel = freeling::SHALLOW;
      an->set_current_invoke_options(ivk);
      an->analyze(ls);      

      // extract actions on parser resutls
      list<action> a = action_extractor.extract_actions(*ls.begin()); // only one sentence expected.
      acts.push_back(make_pair(lin,a));
    }

    // add MSD info to user[0] in heads, so action class can print it if needed
    for (auto &p : acts ) {
      for (auto &a : p.second) {
        if (not a.predH.get_form().empty()) a.predH.user.push_back(tagst[lang]->get_msd_string(a.predH.get_tag()));
        if (not a.objH.get_form().empty()) a.objH.user.push_back(tagst[lang]->get_msd_string(a.objH.get_tag()));
        if (not a.compH.get_form().empty()) a.compH.user.push_back(tagst[lang]->get_msd_string(a.compH.get_tag()));
        if (not a.otherH.get_form().empty()) a.otherH.user.push_back(tagst[lang]->get_msd_string(a.otherH.get_tag()));
      }
    }
    
    answer = toJSON(acts);
  }

  else {
    // normal text, analyze document as a whole, with paragraph=true
    freeling::document doc;
    an->analyze(freeling::util::string2wstring(text), doc, true);
    nw = doc.get_num_words();
    // get FreeLing doc in string (containing XML, JSON, etc) 
    answer = freeling::util::wstring2string(out.find(lang+"#"+outf)->second->PrintResults(doc));
  }  
  
  return answer;
}



string analyzer_pool::toJSON(const list<pair<wstring,list<action>>> &acts) {

  string res = "[";
  for (list<pair<wstring,list<action>>>::const_iterator s=acts.begin(); s!=acts.end(); ++s) {
    if (s!=acts.begin()) res += ", ";
    res += "{";
    res +=  "\"sentence\" : \"" + freeling::util::wstring2string(s->first) + "\", " +
      "\"actions\" : [";
    for (list<action>::const_iterator a=s->second.begin(); a!=s->second.end(); ++a) {
      if (a!=s->second.begin()) res += ", ";
      res += a->asJSON();
    }
    res += "]";
    res += "}";
  }    

  res += "]";
  return res;
}
