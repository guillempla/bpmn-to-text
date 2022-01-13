#ifndef _TS_LOGGER
#define _TS_LOGGER

#include <iostream>

#define LEVEL_ERROR 0
#define LEVEL_WARNING 1
#define LEVEL_LOG 2
#define LEVEL_DEBUG 3

extern int LogLevel;

#ifdef DISABLE_TS_LOG
  #define TS_DEBUG(x)
  #define TS_LOG(x)
  #define TS_WARNING(x)
#else 
  #define TS_DEBUG(x) {if (LogLevel>=LEVEL_DEBUG) std::cerr << x << std::endl;}
  #define TS_LOG(x) {if (LogLevel>=LEVEL_LOG) std::cerr << x << std::endl;}
  #define TS_WARNING(x) {if (LogLevel>=LEVEL_WARNING) std::cerr << x << std::endl;}
#endif

#define TS_ERROR(x) {if (LogLevel>=LEVEL_ERROR) std::cerr << x << std::endl; exit(1);}


#endif
