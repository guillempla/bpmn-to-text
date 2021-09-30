
#include "analyzer_pool.h"
#define WORKER analyzer_pool

/// tell the handler whether to include stats also in response for final user
const bool UserStats=true;

#include "server.cc"
