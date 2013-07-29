#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/config_unit.sh

UNITTEST_CLASS="com.skplanet.cask.test.Runner"
#echo "unit dir:$UNITTEST_DIR"
#echo "unit class:$UNITTEST_CLASS" 
#echo "class path:$CLASSPATH"

#exit 3
#echo "java -Dserver.home=$UNITTEST_DIR -cp $CLASSPATH $UNITTEST_CLASS start"
java -Dserver.home=$UNITTEST_DIR -cp $CLASSPATH $UNITTEST_CLASS start

exit $?