
#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/config.sh

if [ -f $PID ]; then
    if kill -0 `cat $PID`  > /dev/null 2>&1; then
        echo "stopping $PID"
        kill `cat $PID`      
    else
        echo "no process to stop. or are you delete $PID? then kill it yourself."
    fi
else 
    echo "no process to stop. or are you delete $PID? then kill it yourself."
fi
