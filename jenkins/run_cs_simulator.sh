#!/bin/bash -e

KAFKA_TOPIC=cs

docker run -d --rm --name spotify-kafka --tty=true -p 2181:2181 -p $KAFKA_PORT:$KAFKA_PORT --env ADVERTISED_HOST=$KAFKA_HOST --env ADVERTISED_PORT=$KAFKA_PORT spotify/kafka

docker run --rm -e KAFKA_HOST="${KAFKA_HOST}" \
                -e KAFKA_PORT="${KAFKA_PORT}" \
                -e KAFKA_TOPIC="${KAFKA_TOPIC}" \
                --name cs-simulator-kafka -d -p $CS_PORT:$CS_PORT bwsw/cs-simulator-kafka:4.10.3-NP
