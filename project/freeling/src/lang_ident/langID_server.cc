

#include "langID.h"  // worker class to be served
#define WORKER langID

/// tell the handler whether to include stats also in response for final user
const bool UserStats=false;

// generic server code that will handle requests and call the worker
#include "server.cc"

