FROM openjdk:8u131-jdk-alpine

ARG DOCKER_VERSION=17.06.0

RUN apk add --no-cache wget ca-certificates && \
    wget -nv https://download.docker.com/linux/static/stable/x86_64/docker-${DOCKER_VERSION}-ce.tgz -O /tmp/docker.tgz && \
    tar xzf /tmp/docker.tgz -C /tmp/ --strip-components=1 && \
    mv /tmp/docker /usr/local/bin/ && \
    rm /tmp/*
