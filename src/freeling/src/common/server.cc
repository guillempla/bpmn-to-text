
#include <iostream>
#include <string>

/// generic server class
#include "TS_service.h"
#include "util.h"
#include "logger.h"

using namespace std;

/// Server managing requests. 
/// 'WORKER' must be defined to be an existing worker class
/// before including this file.

int LogLevel = 2;
TS_service<WORKER> *server;


/////////////////////////////////////////
///  This function is called by the server each time a request arrives
///  Should be in TS_service class, but Moongoose does not like it inside a class
/////////////////////////////////////////

int handle_request(struct mg_connection *conn, enum mg_event ev) {

  if (ev == MG_AUTH) return MG_TRUE; // authentication event
  if (ev != MG_REQUEST) return MG_FALSE; // other events (non MG_REQUEST)
  if (string(conn->request_method)!="POST") return MG_FALSE; // other events (non-POST request)

  TS_LOG("------------\nRequest received");

  // parse JSON request for required parameters, and extract them
  map<string,string> params;
  TS_generic_service::status rqst = server->extract_request_parameters(conn, "json", params);
  if (rqst == TS_generic_service::ERROR) return MG_TRUE;
  
  // load some specific worker modules or variant if needed (e.g. new language)
  worker_status st = server->adapt_worker_configuration(params);
  if (st.status == WRK_ERROR) {
    server->send_response(conn, "<error><code>400</code><reason>[TS-114] "+st.reason+"</reason></error>");
    return MG_TRUE;
  }
  
  // analyze using appropriate library. 
  string result = server->process(params, UserStats);
  
  // send response
  server->send_response(conn,result);

  TS_LOG("Request attended succesfully");
  return MG_TRUE;
}


/////////////////////////////////////////
///  Main: create the server and enter infinite dispatch loop
/////////////////////////////////////////

int main(int argc, char *argv[]) {

  if (argc<4 or argc>5) 
    TS_ERROR("Usage:  " << argv[0] << " port worker_configfile request_parameter_file [loglevel]");

  string port = string(argv[1]);
  string configfile = string(argv[2]);
  string paramfile = string(argv[3]);
  if (argc==5)
    LogLevel = std::stoi(argv[4]);

  // create server
  TS_LOG("Creating server");
  server = new TS_service<WORKER>(port, configfile, paramfile, handle_request);

  // dispatch clients forever
  server->dispatch_loop();

  // clean up
  delete server;
}
