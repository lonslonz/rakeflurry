DIR=`dirname $0`
HADOOP_CMD="/app/di/hdfs/bin/hadoop" 
HADOOP_TO_PATH="/data/rakeflurry/daily"
LOCAL_FROM_PATH="/app/home/rake/rakeflurry_data"

CURR_DAY=`date +"%Y/%m/%d"`

echo "$HADOOP_CMD fs -mkdir -p /data/rakeflurry/daily/$CURR_DAY"
$HADOOP_CMD fs -mkdir -p /data/rakeflurry/daily/$CURR_DAY

echo "$HADOOP_CMD fs -put $LOCAL_FROM_PATH/$CURR_DAY/* $HADOOP_TO_PATH/$CURR_DAY" 
$HADOOP_CMD fs -put $LOCAL_FROM_PATH/$CURR_DAY/* $HADOOP_TO_PATH/$CURR_DAY 

COUNT=`$HADOOP_CMD fs -ls $HADOOP_TO_PATH/$CURR_DAY | wc -l`
if(($COUNT < 5))
then
    echo "ERROR. count : $COUNT"
    sh $DIR/mail.sh "ERROR: rakeflurry scripts putToHadoop.sh" "count : $COUNT\n"
    exit -1
else
    echo "files count : $COUNT"
    echo $COUNT
    exit 0
fi
