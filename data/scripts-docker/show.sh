#!/usr/bin/env sh
docker run --rm -i -v $4:$3 --workdir=$PWD -v /var/run/docker.sock:/var/run/docker.sock $2 workspace select $1 -no-color
docker run --rm -i -v $4:$3 --workdir=$PWD -v /var/run/docker.sock:/var/run/docker.sock $2 show -no-color