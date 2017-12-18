#!/bin/bash -e

docker run --rm --name resmo-cloudstack-simulator -d -p ${CS_PORT}:${CS_PORT} resmo/cloudstack-sim

ITERATIONS=40
SLEEP=30

echo "wait for CloudStack simulator deploys"

for i in `seq 1 $ITERATIONS`
do
    curl -s -I http://localhost:${CS_PORT}/client/ | head -1 | grep "200"

    if [ $? -eq 0 ]
    then
        echo "OK"
        break
    else
        echo "retry number $i"
        sleep $SLEEP
    fi

    if [ $i -eq $ITERATIONS ]
    then
        exit 1
    fi
done
