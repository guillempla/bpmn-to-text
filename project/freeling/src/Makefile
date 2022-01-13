#
# Usage:
#     make FREELINGDIR=/freeling/install/location [LOGS=-DDISABLE_TS_LOG] 

# default FL installation, can be changed with command line above
FREELINGDIR=/usr/local

INCLUDES=./include
COMMON=./common

all : lang_ident_server analyzer_server 

libTS.so : $(COMMON)/TS_generic_service.cc $(COMMON)/TS_generic_service.h $(COMMON)/TS_service.h $(COMMON)/logger.h $(COMMON)/util.h
	g++ -o libTS.so -shared $(COMMON)/TS_generic_service.cc $(INCLUDES)/mongoose.c $(INCLUDES)/pugixml.cpp $(INCLUDES)/json11.cpp -I$(INCLUDES) -std=gnu++0x -fPIC

analyzer_server : analyzer/analyzer_server.cc analyzer/analyzer_pool.cc analyzer/pool_config.cc analyzer/actionSRL.cc $(COMMON)/server.cc $(COMMON)/worker.h libTS.so analyzer/analyzer_pool.h analyzer/pool_config.h analyzer/actionSRL.h
	g++ -o analyzer_server analyzer/analyzer_server.cc analyzer/analyzer_pool.cc analyzer/pool_config.cc analyzer/actionSRL.cc -I$(INCLUDES) -I$(COMMON) -I$(FREELINGDIR)/include -std=gnu++0x -lrt -lfreeling -lTS -L$(FREELINGDIR)/lib $(LOGS) -lboost_system -lboost_program_options -L.

lang_ident_server : lang_ident/langID_server.cc lang_ident/langID.cc $(COMMON)/server.cc $(COMMON)/worker.h libTS.so lang_ident/langID.h 
	g++ -o lang_ident_server lang_ident/langID_server.cc lang_ident/langID.cc -I$(INCLUDES) -I$(COMMON) -I$(FREELINGDIR)/include -L$(FREELINGDIR)/lib -std=gnu++0x -lrt -lfreeling $(LOGS) -lTS -L.

install :
	cp lang_ident_server analyzer_server $(FREELINGDIR)/bin/
	cp libTS.so $(FREELINGDIR)/lib/
	mkdir -p $(FREELINGDIR)/share/freeling/server-config
	cp -rL analyzer/config $(FREELINGDIR)/share/freeling/server-config/analyzer
	cp -rL lang_ident/config $(FREELINGDIR)/share/freeling/server-config/lang_ident
	cat lang_ident/config/config.cfg | sed 's#$$FREELINGSHARE#$(FREELINGDIR)/share#' > $(FREELINGDIR)/share/freeling/server-config/lang_ident/config.cfg

uninstall:
	rm -rf $(FREELINGDIR)/bin/lang_ident_server $(FREELINGDIR)/bin/analyzer_server $(FREELINGDIR)/share/freeling/server-config $(FREELINGDIR)/lib/libTS.so 

clean :
	rm -f lang_ident_server analyzer_server libTS.so
