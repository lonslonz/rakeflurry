Rakeflurry
==========
Server that collect app statistics data using flurry's api from api.flurry.com.
- send command requests via REST HTTP api.
- get running or finished jobs' information using REST HTTP api.
- copy data to hdfs storage for further processing.
- call api every second (flurry's rate limit)
- when jobs failed or finished, send mail to admin.
- collect stats of last n days.


### Installation

Prerequisite
- install ant, ivy
- more than jdk 1.6.x 
- MySQL 5.0.x
- hadoop server (CDH 4.x or apache hadoop 2.0.x)
- [PrimeMailer] (https://github.com/lonslonz/PrimeMailer)

Install
- download
- unzip
- ant resolve
- ant
- deploy ./PrimMailer-x.x.x.tar.gz and unzip 

Configuration
- ./conf/server.xml : copy from server.default.xml, set your port, hadoop server, PrimeMailer server.
- ./conf/hibernate.cfg.xml : copy from hibernate.cfg.default.xml, set your mysql information. 

DB Configuration
- create database rakeflurry
- create schema using ./sql/create.sql on rakeflurry db.

Server management
- startup : ./bin/start.sh
- stop : ./bin/stop.sh


### API
default address : http://localhost:8100/rakeflurry/

##### /rakeflurry/collect

Description
- collect data from flurry.com using api.
- only a collecting job can be executed at the certain time.
- after getting data, data moved to hdfs storage. 
Method
- HTTP POST
Parameters
- id : your id. for authentification.
- password : your password. id and password must be the same to one specified in server.xml.
- options : options for collecting.
 - duration : get data of last n days.

eg.)
address : http://localhost:8100/rakeflurry/collect
post message : 
```json
{
"id" : "your id",  
"password" : "your password. ",
"options" : {"duration" : "30"}
}
```

##### /rakeflurry/recover
Description
- recover when /rakeflurry/collect fail.
- get failed jobs and retry those.
- working is the same with /rakeflurry/collect except about error job.
- Only a job can be executed; /rakeflurry/collect and /rakeflurry/recover can't be executed at the same time.
Method
- HTTP POST
Parameters
- id : your id. for authentification.
- password : your password. id and password must be the same to one specified in server.xml.
- options : options for collecting.
 - duration : get data of last n days.
 - dashboard_id : id that want recover. dashboard id returned by /rakeflurry/show.

eg.)
address : 

``
http://localhost:8100/rakeflurry/collect
``

post message : 
```json
{
"id" : "your id",  
"password" : "your password. ",
"options" : {"duration" : "30", "dashboard_id": "id number that returned from show api."}
}
```

##### /rakeflurry/collect/show
Description
- show status about running or finished jobs.
- after /rakeflurry/collect executed, check this api periodically, can know complete or error jobs. 
- dashboard is a running, finished job result history.
Method
- HTTP GET
Parameters
- id (optional) : dashboard_id to show
- date (optional) : all dashboard yyyy-MM-dd format date to show
- recover (optional) : include recovery dashboard job results.
- showapi (optional) : when true, return detailed api calling results.

eg.)
``
http://localhost:8100/rakeflurry/collect/show
``

##### /rakeflurry/keymap/show
Description
- show keymap data that is saved by calling /keymap/overwrite.
Method
- HTTP GET
Parameters

eg.)
```json
{
    "results": [
        {
            "mbrNo": "anyid in flurry",
            "accessCode": "ACCESS CODE FROM FLURRY",
            "apiKeys": [
                "API KEY for app",
                "API KEY for app"
            ]
        }
    ]
}
```

##### /rakeflurry/keymap/overwrite
Description
- overwrute keymap data that is composed access code, api key.
Method
- HTTP POST
Parameters
- id : your id. for authentification.
- password : your password. id and password must be the same to one specified in server.xml.
- keymap
 - mbrNo : unique id
 - access code : received access code from flurry.com
 - api keys : api key list that want collect data.

eg.)
``
http://localhost:8100/rakeflurry/keymap/overwrite
``


```json
{
    "id": "your id",
    "password": "your pass",
    "keymap": [
        {
            "mbrNo": "anyid in flurry",
            "accessCode": "ACCESS CODE FROM FLURRY",
            "apiKeys": [
                "API KEY for app",
                "API KEY for app"
            ]
        }
    ]
}
```
