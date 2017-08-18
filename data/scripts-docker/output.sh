#!/usr/bin/env sh
docker run -i -v $PWD:/data --workdir=/data -v /var/run/docker.sock:/var/run/docker.sock $2  output -no-color -json