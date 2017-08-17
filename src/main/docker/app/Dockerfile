FROM openjdk:8u131-jre-alpine

ARG TERRAFORM_VERSION=0.9.11

# Install terraform
RUN apk add --no-cache bash ca-certificates git openssl unzip wget mariadb-client && \
    wget -nv https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip -O /tmp/terraform.zip && \
    unzip /tmp/terraform.zip -d /usr/bin/ && \
    rm /tmp/terraform.zip


ADD test-env-runner-0.1.jar app.jar
COPY scripts /data/scripts

VOLUME ["/data"]
RUN sh -c 'touch /app.jar'

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -Dspring.profiles.active=container -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
