
#include <iostream>
#include <string>

#include "analyzer_pool.h"

/////////////////////////////////////////
///  Main: create the server and enter infinite dispatch loop
/////////////////////////////////////////

int main(int argc, char *argv[]) {

  freeling::util::init_locale(L"default");

  if (argc!=4) {
    cerr << "Usage:  " << argv[0] << " configdir language output" << endl; 
    exit(1);
  }
  string configdir = string(argv[1]);
  string lang = string(argv[2]);
  string outf = string(argv[3]);

  // create pool of FL analyzers
  analyzer_pool analyzers(configdir);

  // check request parameters
  if (analyzers.check_parameter("language", lang)==analyzer_pool::ERROR) {
    cout << "<error><code>400</code><reason>[TS-114] Value '" + lang + "' not supported for parameter 'language'.</reason></error>";
    exit(0);
  }
  if (analyzers.check_parameter("output", outf)==analyzer_pool::ERROR) {
    cout << "<error><code>400</code><reason>[TS-114] Value '" + outf + "' not supported for parameter 'output'.</reason></error>";
    exit(0);
  }

  // load requested language and output handler
  analyzers.load_language(lang);

  // get text from stdin
  string line,text;
  while (getline(cin, line))
    text = text+line+"\n";

  // process using FreeLing, print result to stdout
  cout << analyzers.analyze(lang, text, outf) << endl;

}
