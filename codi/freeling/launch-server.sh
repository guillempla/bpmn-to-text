#! /bin/bash

SERVER=$1
PORT=$2
CONFIG=$3
PARAMS=$4

while (true); do
    # launch server
    echo "Launching $SERVER"
    $SERVER $PORT $CONFIG $PARAMS
    # server died. Allow some time for port to be freed before re-launching it
    echo "Server $SERVER died. Restarting in 5s"
    sleep 5
done

