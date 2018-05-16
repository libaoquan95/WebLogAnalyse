#!/bin/bash

while [ 1 ]; do
    ./sample_web_log.py > test.log

    fileDir="/home/libaoquan/Documents/nginx/log"

    tmplog="access.`date +'%s'`.log"
    cp test.log $fileDir/tmp/$tmplog
    mv $fileDir/tmp/$tmplog $fileDir
    echo "`date +"%F %T"` generating $tmplog succeed"
    sleep 1
done
