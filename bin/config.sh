
#!/bin/bash

MAIN_CLASS="com.skplanet.cask.Cask"

this="$0"
bin=`dirname "$this"`
script=`basename "$this"`
bin=`cd "$bin"; pwd`
this="$bin/$script"

export SERVER_HOME=`dirname "$this"`/..

LIB_DIR="$SERVER_HOME/lib"
BIN_DIR="$SERVER_HOME/bin"
CONF_DIR="$SERVER_HOME/conf"
TEMP_DIR="$SERVER_HOME/temp"
UNITTEST_DIR="$SERVER_HOME/test"

CLASSPATH=$CLASSPATH:$CONF_DIR

for BIN_FILE in $BIN_DIR/*.jar
do
    CLASSPATH=$CLASSPATH:$BIN_FILE
done

for LIB_FILE in $LIB_DIR/*
do
     CLASSPATH=$CLASSPATH:$LIB_FILE
done


PID=$TEMP_DIR/process.pid


