#ifndef _TS_GENERIC_SERVER
#define _TS_GENERIC_SERVER

#include <map>
#include <set>
#include <string>
#include "mongoose.h"
#include "json11.hpp"


/////////////////////////////////////////////////////////
///  Class cpuclock is a simple class to ease CPU time usage computation
/////////////////////////////////////////////////////////

class cpuclock {
    private:
      struct timespec tstart;
    public:
      cpuclock();
      ~cpuclock();
      void start(); 
      double get_time() const;
};


/////////////////////////////////////////////////////////
///
///  Class TS_generic_service provides the infrastructure to
///  create services for TextServer
///
/////////////////////////////////////////////////////////


class request_parameter {
 public:
  std::string def_value;
  std::set<std::string> valid;
  request_parameter(const std::string &v, const std::set<std::string> &a) : def_value(v), valid(a) {};
};


class TS_generic_service {

 public:
    /// return status for xml handling operations
    typedef enum {OK,ERROR} status;

  private:
    /// underlying mongoose server
    struct mg_server *server;

    /// store expected request parameters
    std::map<std::string,request_parameter> default_values;

    status xml_to_map(struct mg_connection *conn, std::map<std::string,std::string> &req) const;
    status json_to_map(struct mg_connection *conn, std::map<std::string,std::string> &req) const;
    
  public:
    /// constructor
    TS_generic_service(const std::string &port,
                       const std::string &parFile,
                       mg_handler_t handler);
    /// destructor
    ~TS_generic_service();
    /// service loop
    void dispatch_loop();
    /// parse XML request and extract requested parameters
    status extract_request_parameters(struct mg_connection *conn, const std::string &format, std::map<std::string,std::string> &param) const;
    /// send XML response to client
    void send_response(struct mg_connection *conn, const std::string &msg) const;
    /// send HTTP error response
    void send_error(struct mg_connection *conn, int code, const std::string &tscode, const std::string &msg) const;


    //// parse json request and copy pairs to a map
    std::map<std::string,std::string> extract_json_request_parameters(const std::string &request) const;
    //// convert given map to json dictionary
    std::string build_json_response(const std::map<std::string,std::string> &data) const;


};

#endif
