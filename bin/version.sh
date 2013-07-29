
#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/config.sh

java -Dserver.home=$SERVER_HOME -cp $CLASSPATH $MAIN_CLASS version

