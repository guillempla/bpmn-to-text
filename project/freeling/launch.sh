#! /bin/bash

export FREELINGDIR=/usr/local
export FREELINGSHARE=$FREELINGDIR/share/freeling
export FREELINGBIN=$FREELINGDIR/bin

launch-server.sh $FREELINGBIN/lang_ident_server 50005 $FREELINGSHARE/server-config/lang_ident/config.cfg $FREELINGSHARE/server-config/lang_ident/parameters.cfg &
launch-server.sh $FREELINGBIN/analyzer_server 60006 $FREELINGSHARE/server-config/analyzer/config.cfg $FREELINGSHARE/server-config/analyzer/parameters.cfg &

wait

