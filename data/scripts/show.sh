#!/usr/bin/env bash
docker run -i -v $PWD:/data --workdir=/data -v /var/run/docker.sock:/var/run/docker.sock $2 workspace select $1 -no-color
docker run -i -v $PWD:/data --workdir=/data -v /var/run/docker.sock:/var/run/docker.sock $2 show -no-color