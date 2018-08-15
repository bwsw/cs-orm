ITERATIONS=40
SLEEP=30

echo "wait for CloudStack simulator to deploy"

for i in `seq 1 ${ITERATIONS}`
do
    curl -s -I http://${CS_HOST}:${CS_PORT}/client/ | head -1 | grep "200"

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