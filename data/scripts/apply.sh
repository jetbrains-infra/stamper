#!/usr/bin/env sh
echo Executing operation: terraform init
terraform init -no-color

echo Executing operation: terraform get
terraform get -no-color

echo Executing operation: terraform env new $1
terraform env new $1 -no-color

echo Executing operation: terraform env select $1
terraform env select $1 -no-color

echo Executing operation: terraform apply
terraform apply -no-color