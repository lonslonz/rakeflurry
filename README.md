Rakeflurry
==========
Server that collect app statistics data using flurry's api from api.flurry.com.
- send command requests via REST HTTP api.
- get running or finished jobs' information using REST HTTP api.
- copy data to hdfs storage for further processing.
- call api every second (flurry's rate limit)
- when jobs failed or finished, send mail to admin.


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
- collect data from 
- Post message를 json으로 정의
Method
- HTTP POST
Parameters
- Completely the same as parameters of /mail/send. Only difference is using son. 
