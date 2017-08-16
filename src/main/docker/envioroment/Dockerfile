FROM openjdk:8u131-jdk-alpine

ARG TERRAFORM_VERSION=0.9.11

# Install terraform
RUN apk add --no-cache bash ca-certificates git openssl unzip wget mariadb-client && \
    wget -nv https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip -O /tmp/terraform.zip && \
    unzip /tmp/terraform.zip -d /usr/bin/ && \
    rm /tmp/terraform.zip
