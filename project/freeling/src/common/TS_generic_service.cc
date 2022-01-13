#include <iostream>
#include <fstream>
#include <sstream>
#include <cstring>

#include "pugixml.hpp"

#include "logger.h"
#include "util.h"
#include "TS_generic_service.h"

using namespace std;


/// class cpuclock implementation

cpuclock::cpuclock() {};
cpuclock::~cpuclock() {};
void cpuclock::start() {
  clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &tstart);
}
double cpuclock::get_time() const {
  struct timespec tend;
  clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &tend);
  return (1.0E9*double(tend.tv_sec - tstart.tv_sec) + tend.tv_nsec - tstart.tv_nsec)/1.0E9;
} 


////////////////////////////////////////////
///  Constructor: create server, set port
////////////////////////////////////////////

TS_generic_service::TS_generic_service(const string &port,
                                       const std::string &parFile,
                                       mg_handler_t handler) {

  // read and store parameter file content in "default_values"
  ifstream fs;
  fs.open(parFile.c_str());
  if (fs.fail()) 
    TS_ERROR("Error opening parameter file " << parFile)

  string par,def,vals;
  while (fs >> par >> def >> vals) {
    def = (def=="REQUEST" ? "" : def);
    set<string> valid;
    if (vals != "-") {
      valid = util::string_to<set<string>,string>(vals, ",");
      if (def!="" and valid.find(def)==valid.end()) 
        TS_ERROR("Default value '" << def << "' for parameter '"<< par << "' is not in valid values list (" << vals << ")");
    }
    
    default_values.insert(make_pair(par, request_parameter(def,valid)));
  }
  fs.close();
  
  // Create and configure the server
  server = mg_create_server(NULL, handler);
  const char *s = mg_set_option(server, "listening_port", port.c_str()); 
  if (s != NULL)
    TS_ERROR(string(s)); 

}

////////////////////////////////////////////
///  Destructor
////////////////////////////////////////////

TS_generic_service::~TS_generic_service() {
  mg_destroy_server(&server);
}

////////////////////////////////////////////
///  Dispach requests ininitely.
////////////////////////////////////////////

void TS_generic_service::dispatch_loop() {
  // Serve requests. Hit Ctrl-C to terminate the program
  TS_LOG("Listening on port " << string(mg_get_option(server, "listening_port")));
  for (;;) 
    mg_poll_server(server, 5000);    
}

////////////////////////////////////////////
///  extract parameters from XML request into a map
////////////////////////////////////////////

TS_generic_service::status TS_generic_service::xml_to_map(struct mg_connection *conn, map<string,string> &m) const {
  // extract XML request from connection
  pugi::xml_document doc;
  pugi::xml_parse_result pr = doc.load_buffer(conn->content,conn->content_len);
  if (pr.status!=pugi::status_ok) {
    send_error(conn, 400, "TS-160", "Ill-formed XML in request: "+string(pr.description()));
    return TS_generic_service::ERROR;
  }

  // parse XML request 
  pugi::xml_node top = doc.document_element();

  m.clear();
  for (pugi::xml_node x = top.first_child(); x.type()!=pugi::node_null; x=x.next_sibling())
    m.insert(make_pair(x.name(),x.first_child().value()));

  return TS_generic_service::OK;
}

////////////////////////////////////////////
///  extract parameters from json request into a map
////////////////////////////////////////////

TS_generic_service::status TS_generic_service::json_to_map(struct mg_connection *conn, map<string,string> &m) const {
  
  string request(conn->content,conn->content_len);

  string err;
  json11::Json js = json11::Json::parse(request, err);
  json11::Json::object obj = js.object_items();

  m.clear();
  for (auto x : obj) {
    m.insert(make_pair(x.first,x.second.string_value()));
  }

  return TS_generic_service::OK;
}


////////////////////////////////////////////
///  Parse XML request and extrac parameters
/// given as keys in "params" map
////////////////////////////////////////////

TS_generic_service::status TS_generic_service::extract_request_parameters(struct mg_connection *conn,
                                                                          const string &format,
                                                                          map<string,string> &param) const {
  TS_generic_service::status st;
  // extract values from request
  map<string,string> request;
  if (format == "xml")
    st = xml_to_map(conn,request);
  else if (format == "json") 
    st = json_to_map(conn,request);
  else {
    send_error(conn, 400, "TS-160", "Unexpected request format: "+format);
    st = TS_generic_service::ERROR;    
  }
  if (st == TS_generic_service::ERROR) return st;

  // warn about unexpected parameters
  for (auto r : request) 
    if (default_values.find(r.first)==default_values.end())
      TS_WARNING("WARNING: Ignoring unexpected request parameter '" << r.first << "'");      

  // load default values, to be overwriten with request parameters.
  for (auto p : default_values)
    param.insert(make_pair(p.first, p.second.def_value));
  
  // for each parameter, if value is provided by request, overwrite default value
  for (map<string,string>::iterator p=param.begin(); p!=param.end(); p++) {

    auto r = request.find(p->first);
    if (r != request.end()) {
      // if parameter needs checking, check whether the given value is admissible
      auto df = default_values.find(p->first);
      if (df != default_values.end()
          and not df->second.valid.empty()
          and df->second.valid.find(r->second) == df->second.valid.end()) {

        send_error(conn, 400, "TS-114", "Value '"+r->second+"' not supported for parameter '"+p->first+"'");
        return TS_generic_service::ERROR;       
      }

      // parameter found in request, with admissible value. Use value provided by request
      p->second = r->second;
    }

    // parameter not found in request, and no default value defined (i.e. required parameter)
    else if (p->second.empty()) { 
      send_error(conn, 400, "TS-114", "Missing required request parameter '"+p->first+"'");
      return TS_generic_service::ERROR;
    }

    // otherwise (parameter not found in request, but default value available), leave default value.
  }

  return TS_generic_service::OK;
} 

////////////////////////////////////////////
///  Send a XML response to client
////////////////////////////////////////////

void TS_generic_service::send_response(struct mg_connection *conn, const string &msg) const {
  mg_send_header(conn, "Content-Type", "application/json; charset=utf-8");
  mg_printf_data(conn, "%s\n", msg.c_str() );   
}

////////////////////////////////////////////
///  Send a XML response to client
////////////////////////////////////////////

void TS_generic_service::send_error(struct mg_connection *conn, int code, const string &tscode, const string &msg) const {

  TS_WARNING("ERROR: "<<msg);

  string reason = "["+tscode+"] "+msg;
  
  pugi::xml_document doc;
  pugi::xml_node err = doc.append_child("error");
  err.append_child("code").append_child(pugi::node_pcdata).set_value(std::to_string(code).c_str());
  err.append_child("reason").append_child(pugi::node_pcdata).set_value(reason.c_str());

  ostringstream ss;
  doc.save(ss, "", pugi::format_raw | pugi::format_no_declaration);

  send_response(conn, ss.str());
}


////////////////////////////////////////////
//// convert given map to json dictionary
////////////////////////////////////////////

string TS_generic_service::build_json_response(const map<string,string> &data) const {
  json11::Json js(data);
  return js.dump();
}
