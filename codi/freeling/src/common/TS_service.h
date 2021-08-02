#ifndef _TS_SERVER
#define _TS_SERVER

#include <ctime>
#include <map>
#include <string>
#include <sstream>

#include "pugixml.hpp"

#include "logger.h"
#include "TS_generic_service.h"


/////////////////////////////////////////////////////////
/// Class worker_status holds the result of a worker call
/////////////////////////////////////////////////////////

typedef enum {WRK_OK, WRK_ERROR} wrk_status;
class worker_status {
 public:
  wrk_status status;
  std::string reason;
};


/////////////////////////////////////////////////////////
///
///  Class TS_service is a derived class for a server
///  using a given kind of worker.
///
/////////////////////////////////////////////////////////

template <class WRK>
class TS_service : public TS_generic_service {

  private:
    /// underlying worker
    WRK *worker;

  public:
    /// constructor 
    TS_service(const std::string &port,
               const std::string &cfgFile,
               const std::string &parFile,
               mg_handler_t handler);
    
    /// destructor
    ~TS_service();

    /// see if request requires some specific configuration of the worker
    /// (e.g. loading a new language)
    worker_status  adapt_worker_configuration(const std::map<std::string,std::string> &params);
    
    /// process data with worker
    std::string process(const std::map<std::string,std::string> &params, bool UserStats=false);

    /// build XML response to be returned to caller
    static std::string buildXMLresponse(std::string &answer, const std::string &content_type,
                                        const std::string &nw, const std::string &cput,
                                        bool UserStats);    
};


/// class TS_service implementation 

 
//////////////////////////////////////////
/// constructor

template <class WRK>
TS_service<WRK>::TS_service(const std::string &port,
                            const std::string &cfgFile,
                            const std::string &parFile,
                            mg_handler_t handler) : TS_generic_service(port, parFile, handler) {
                         
    // create worker to process requests
    worker = new WRK(cfgFile);
}
    

//////////////////////////////////////////
/// destructor

template <class WRK>
TS_service<WRK>::~TS_service() { delete worker; }


//////////////////////////////////////////
/// see if request requires some specific configuration of the worker
/// (e.g. loading a new language)

template <class WRK>
worker_status TS_service<WRK>::adapt_worker_configuration(const std::map<std::string,std::string> &params) {
  return worker->adapt_configuration(params);
}

//////////////////////////////////////////
/// process data with worker, create final response

template <class WRK>
std::string TS_service<WRK>::process(const std::map<std::string,std::string> &params, bool UserStats) {

  // start CPU time clock
  cpuclock clock;
  clock.start();
  
  // do actual processing
  int nw;
  std::string result = worker->process(params, nw);

  /*
  // check clock and compute time
  double cput = clock.get_time();
  
  // get requested output format. If unknown, assume plain text
  auto f = params.find("output");
  std::string format = (f==params.end() ? "text" : f->second);
  
  // determine content_type for HTML header (xml, json, text)
  std::string content_type;
  if (format=="json") content_type="json";
  else if (format=="xml" or format=="naf") content_type="xml";
  else content_type="text";
  
  return buildXMLresponse(result, content_type, std::to_string(nw), std::to_string(cput), UserStats); 
  */

  return result;
}


////////////////////////////////////////////
/// build XML response for TS, with needed stats
  
template <class WRK>
std::string TS_service<WRK>::buildXMLresponse(std::string &answer, const std::string &content_type,
                                              const std::string &nw, const std::string &cput,
                                              bool UserStats) {    
  // create response XML
  pugi::xml_document doc;
  pugi::xml_node resp = doc.append_child("response");
  resp.append_child("outputformat").append_child(pugi::node_pcdata).set_value(content_type.c_str());
  resp.append_child("wordcount").append_child(pugi::node_pcdata).set_value(nw.c_str());
  resp.append_child("cputime").append_child(pugi::node_pcdata).set_value(cput.c_str());   
  pugi::xml_node output = resp.append_child("output");
  
  // add actual worker response inside '<output>' node
  if (content_type=="xml") {
    // if it is XML, add as subtree
    pugi::xml_document xmlres;
    pugi::xml_parse_result pr = xmlres.load_buffer(answer.c_str(), answer.length());
    if (pr.status!=pugi::status_ok) 
      TS_WARNING("Problems parsing XML output from worker: "+std::string(pr.description()));
    
    // get new subtree to be added      
    pugi::xml_node doc = xmlres.first_child();
    
    // if we need to add stats for the user, add as XML nodes
    if (UserStats) {
      doc.prepend_child("cputime").prepend_child(pugi::node_pcdata).set_value(cput.c_str());   
      doc.prepend_child("wordcount").prepend_child(pugi::node_pcdata).set_value(nw.c_str());
    }
    
    // create node from 'answer' under <output>
    output.prepend_copy(doc);
  }
  else {
    // if we need to add stats for the user, add depending on the format
    if (UserStats) {
      if (content_type=="text") {
        answer.insert(0, "## cputime="+cput+" wordcount="+nw+"\n");
      }
      else if (content_type=="json") {
        size_t p = answer.find("{"); // locate first "{"
        answer.insert(p+1, 
                      " \"cputime\" : \"" + cput + "\"," +
                      " \"wordcount\" : \"" + nw + "\",\n");
      }
    }
    
    // non-XML, add as pcdata under <output>     
    output.append_child(pugi::node_pcdata).set_value(answer.c_str());    
  }
  
  // convert xmldoc to string and return it
  std::ostringstream ss;
  doc.save(ss, "", pugi::format_raw | pugi::format_no_declaration);
  return ss.str();
}

#endif
