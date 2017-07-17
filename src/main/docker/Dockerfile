FROM openjdk:8u131-jre-alpine
VOLUME /tmp
ADD test-env-runner-0.1.jar app.jar
ENV TERRAFORM_VERSION=0.9.11

VOLUME ["/data"]

#WORKDIR /data

#ENTRYPOINT ["/usr/bin/terraform"]

#CMD ["--help"]

RUN apk update && \
    apk add bash ca-certificates git openssl unzip wget && \
    cd /tmp && \
    wget https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip && \
    unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip -d /usr/bin && \
    rm -rf /tmp/* && \
    rm -rf /var/cache/apk/* && \
    rm -rf /var/tmp/*

ENV PATH="/usr/bin/terraform:${PATH}"

RUN sh -c 'touch /app.jar'

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
