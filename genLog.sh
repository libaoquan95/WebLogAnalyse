#!/bin/bash

while [ 1 ]; do
    ./sample_web_log.py > test.log

    tmplog="access.`date +'%s'`.log"
    cp test.log Documents/nginx/log/tmp/$tmplog
    mv Documents/nginx/log/tmp/$tmplog Documents/nginx/log/
    echo "`date +"%F %T"` generating $tmplog succeed"
    sleep 1
done
