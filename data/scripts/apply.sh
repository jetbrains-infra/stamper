#!/usr/bin/env sh
echo Executing operation: terraform init
/opt/terraform/terraform init -no-color

echo Executing operation: terraform get
/opt/terraform/terraform get -no-color

echo Executing operation: terraform env new $1
/opt/terraform/terraform workspace new $1 -no-color

echo Executing operation: terraform env select $1
/opt/terraform/terraform workspace select $1 -no-color

echo Executing operation: terraform apply
/opt/terraform/terraform apply -no-color