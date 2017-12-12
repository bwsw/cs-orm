#!/bin/bash -e

echo "---------------------------------------------"
echo "----------------- Unit tests ----------------"
echo "---------------------------------------------"

sbt clean coverage test coverageReport

echo "---------------------------------------------"
echo "-------------- Integration tests ------------"
echo "---------------------------------------------"

docker run --rm --name resmo-simulator-kafka -d -p ${CS_PORT}:${CS_PORT} resmo/cloudstack-sim

ITERATIONS=40
SLEEP=30

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

sbt it:test

echo "---------------------------------------------"
echo "-------------- Scalastyle checks ------------"
echo "---------------------------------------------"

sbt scalastyle

sbt test:scalastyle

echo "git branch: $GIT_BRANCH"
if [ -n "$GIT_BRANCH" ]; then
    if [ "$GIT_BRANCH" = "origin/master" ]; then

        echo "---------------------------------------------"
        echo "------- Publish to Maven repository ---------"
        echo "---------------------------------------------"

        sbt publish
	fi
fi
