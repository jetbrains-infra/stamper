#!/usr/bin/env sh
docker run --rm -i -v $4:$3 --workdir=$PWD -v /var/run/docker.sock:/var/run/docker.sock $2 output -no-color -json