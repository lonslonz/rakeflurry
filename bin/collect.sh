ADDR="http://localhost:8100/rakeflurry/collect"
ACCEPT="Accept: application/json"
CONTENT="Content-type:: application/json"

POST=" {
        \"id\" : \"lons\",
        \"password\" : \"dlwhdals\",
        \"options\" : {\"duration\" : \"30\", \"multi\" : \"true\"}
    }
"
#echo "curl -v -H '$ACCEPT' -H '$CONTENT' -X POST -d '${POST}' $ADDR"
curl  -H "$ACCEPT" -H "$CONTENT" -X POST -d "${POST}" $ADDR
