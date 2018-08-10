#!/bin/bash -e

KAFKA_TOPIC=cs

docker run -d --rm --name spotify-kafka --tty=true -p 2181:2181 -p $KAFKA_PORT:$KAFKA_PORT --env ADVERTISED_HOST=$KAFKA_HOST --env ADVERTISED_PORT=$KAFKA_PORT spotify/kafka

docker run --rm -e KAFKA_HOST="${KAFKA_HOST}" \
                -e KAFKA_PORT="${KAFKA_PORT}" \
                -e KAFKA_TOPIC="${KAFKA_TOPIC}" \
                --name cs-simulator-kafka -d -p $CS_PORT:$CS_PORT bwsw/cs-simulator-kafka:4.10.3-NP

ITERATIONS=40
SLEEP=30

echo "wait for CloudStack simulator deploys"

for i in `seq 1 ${ITERATIONS}`
do
    curl -s -I http://localhost:${CS_PORT}/client/ | head -1 | grep "200"

    if [ $? -eq 0 ]
    then
        echo "OK"
        break
    else
        echo "retry number $i"
        sleep ${SLEEP}
    fi

    if [ ${i} -eq ${ITERATIONS} ]
    then
        exit 1
    fi
done
