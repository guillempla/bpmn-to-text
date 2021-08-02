
#ifndef _WORKER
#define _WORKER

#include <string>
#include <map>

#include "TS_service.h"
// base class to derive workers for TS services

class worker {

 public:
    worker() {};
    ~worker() {};

    // adapt worker confguration to the needs of a specific request
    // (eg.g loading a new language)
    // Default : do nothing
    virtual worker_status adapt_configuration(const std::map<std::string,std::string> &params) {
      worker_status ws;
      ws.status = WRK_OK;
      return ws;
    };

    // process request. Child class must have this method.
    virtual std::string process(const std::map<std::string,std::string> &params, int &nw) = 0;

};


#endif
