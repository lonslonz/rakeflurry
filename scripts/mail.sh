ADDR="http://172.22.212.67:8200/mail/sendpost"
ACCEPT="Accept: application/json"
CONTENT="Content-type:: application/json"

CURR=`date +"%Y-%m-%d %H:%M:%S"`
SUBJECT=$1
BODY=$2

POST=" {
        \"from\" : \"rakeflurry@sk.com\",
        \"to\" : \"rakeflurry@sk.com\",
        \"subject\" : \"$SUBJECT\",
        \"msg\" : \"$CURR\n$BODY\"
    }
"
echo "curl -v -H '$ACCEPT' -H '$CONTENT' -X POST -d '${POST}' $ADDR"
curl  -H "$ACCEPT" -H "$CONTENT" -X POST -d "${POST}" $ADDR
