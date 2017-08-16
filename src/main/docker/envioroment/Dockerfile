FROM openjdk:8u131-jre-alpine
VOLUME /tmp

ENV TERRAFORM_VERSION=0.9.11


# Install terraform
RUN apk add --no-cache bash ca-certificates git openssl unzip wget mariadb-client && \
    cd /tmp && \
    wget https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip && \
    unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip -d /usr/bin

ENV PATH="/usr/bin/terraform:${PATH}"
