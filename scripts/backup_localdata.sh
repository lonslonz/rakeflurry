#!/bin/sh

COMPRESS_DAY=+7
REMOVE_DAY=+30

LOG_DIR=$1

if(($# != 1))
then
    echo "Usage : backup.sh [log dir]"
    exit -1
fi

echo "dest dir : $LOG_DIR"

if [ ! -d $LOG_DIR ]
then
    echo "there is no dir : $LOG_DIR"
    exit -1
fi

echo "compress file : $COMPRESS_DAY"
if [ -n $LOG_DIR ]
then
    echo 'find $LOG_DIR -name "*.log" -mtime $COMPRESS_DAY | xargs -i gzip {}'
    find $LOG_DIR -name "*.log" -mtime $COMPRESS_DAY | sort
    find $LOG_DIR -name "*.log" -mtime $COMPRESS_DAY | xargs -i gzip {}
else
    echo "LOG_DIR is empty"
    exit -1
fi

echo "remove file : $REMOVE_DAY"
if [ -n $LOG_DIR ]
then
    echo 'find $LOG_DIR -name "*.gz" -mtime $REMOVE_DAY | xargs -i rm -f {}'
    find $LOG_DIR -name "*.gz" -mtime $REMOVE_DAY | sort
    find $LOG_DIR -name "*.gz" -mtime $REMOVE_DAY | xargs -i rm -f {}
else
    echo "LOG_DIR is empty"
    exit -1
fi
