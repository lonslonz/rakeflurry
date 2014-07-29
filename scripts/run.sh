echo -e "\n-------- start : `date` --------"
DIR=`dirname $0`
echo "DIR : $DIR"
echo "$DIR/copyFromRemote.sh"
sh $DIR/copyFromRemote.sh
EXIT=$?
if(($EXIT != 0))
then
    echo "ERROR: copyFromRemote.sh"
    exit -1
else 
    echo "SUCCESS:copyFromRemote.sh"
fi

echo "$DIR/putToHadoop.sh"
sh $DIR/putToHadoop.sh
EXIT=$?
if(($EXIT != 0))
then
    echo "ERROR: putToHadoop.sh"
    exit -1
else 
    echo "SUCCESS:putToHadoop.sh"
fi

echo '$DIR/mail.sh "SUCCESS: rakeflurry scripts" "Copy done. from RAKEFLYap1 to Hadoop."'
sh $DIR/mail.sh "SUCCESS: rakeflurry scripts" "Copy done. from RAKEFLYap1 to Hadoop."

echo -e "\n-------- finish : `date` --------"
