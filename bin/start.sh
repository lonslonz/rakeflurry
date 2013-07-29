
#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/config.sh

JAVA_OPT="-Xmx500m -Xms100m -XX:+UseConcMarkSweepGC -server "
JMX_CONFIG="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8998 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false"

mkdir -p "$TEMP_DIR"
if [ -f $PID ]; then
    if kill -0 `cat $PID` > /dev/null 2>&1; then
        echo "server running as process `cat $PID`. stop it."
        echo "when server already killed, delete $PID."
        exit 1
    fi
fi
java -Dserver.home=$SERVER_HOME $JAVA_OPT $JMX_CONFIG -cp $CLASSPATH $MAIN_CLASS start &

echo $! > $PID
echo "Starting server : $PID"
sleep 1
head $PID
