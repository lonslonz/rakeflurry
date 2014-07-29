DIR=`dirname $0`
USER_ID=1000653
SERVER=172.19.105.116
REMOTE_FROM_PATH="/home/1000653/rakeflurry_data"
LOCAL_TO_PATH="/app/home/rake/rakeflurry_data"

CURR_DAY=`date +"%Y/%m/%d"`

if [ ! -d "$LOCAL_TO_PATH/$CURR_DAY" ]
then
echo "dir not exists: $LOCAL_TO_PATH/$CURR_DAY"
echo "so make it"
echo "mkdir -p $LOCAL_TO_PATH/$CURR_DAY"
mkdir -p $LOCAL_TO_PATH/$CURR_DAY
fi

echo "scp -rp $USER_ID@$SERVER:$REMOTE_FROM_PATH/$CURR_DAY/* $LOCAL_TO_PATH/$CURR_DAY"
scp -rp $USER_ID@$SERVER:$REMOTE_FROM_PATH/$CURR_DAY/* $LOCAL_TO_PATH/$CURR_DAY

COUNT=`ls -al $LOCAL_TO_PATH/$CURR_DAY | wc -l`
if(($COUNT < 5))
then
    echo "ERROR. count : $COUNT"
    sh $DIR/mail.sh "ERROR: rakeflurry scripts copyFromRemote.sh" "count : $COUNT\n"
    exit -1
else
    echo "files count : $COUNT"
    echo $COUNT
    exit 0
fi
