#!/bin/bash

start=$(date +"%s")

ssh ${SERVER_USER}@${SERVER_HOST} -i key.txt -t -t -o StrictHostKeyChecking=no << 'ENDSSH'
docker pull daktil/scifidicebackend:v1

CONTAINER_NAME=scifidiceapp
if [ "$(docker ps -qa -f name=$CONTAINER_NAME)" ]; then
    if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
        echo "Container is running -> stopping it..."
        docker stop $CONTAINER_NAME;
    fi
fi

docker run -d --rm -p 8083:8083 --name $CONTAINER_NAME daktil/scifidicebackend:v1

exit
ENDSSH

end=$(date +"%s")

diff=$(($end - $start))

echo "Deployed in : ${diff}s"
