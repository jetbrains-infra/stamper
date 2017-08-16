FROM openjdk:8u131-jre-alpine
VOLUME /tmp
ADD test-env-runner-0.1.jar app.jar
COPY scripts /data/scripts
ENV TERRAFORM_VERSION=0.9.11

VOLUME ["/data"]

# Install terraform
RUN apk add --no-cache bash ca-certificates git openssl unzip wget mariadb-client && \
    cd /tmp && \
    wget https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip && \
    unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip -d /usr/bin

ENV PATH="/usr/bin/terraform:${PATH}"

RUN sh -c 'touch /app.jar'


ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -Dspring.profiles.active=container -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
