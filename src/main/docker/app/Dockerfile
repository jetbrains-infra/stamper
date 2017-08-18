FROM openjdk:8u131-jre-alpine

ARG DOCKER_VERSION=17.06.0

RUN apk add --no-cache wget ca-certificates && \
    wget -nv https://download.docker.com/linux/static/stable/x86_64/docker-${DOCKER_VERSION}-ce.tgz -O /tmp/docker.tgz && \
    tar xzf /tmp/docker.tgz -C /tmp/ --strip-components=1 && \
    mv /tmp/docker /usr/local/bin/ && \
    rm /tmp/*


ADD test-env-runner-0.1.jar app.jar
COPY scripts-docker /data/scripts-docker

VOLUME ["/data"]
RUN sh -c 'touch /app.jar'
RUN mkdir /data/stacks
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -Dspring.profiles.active=container -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
