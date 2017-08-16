#!/usr/bin/env sh
echo Executing operation: terraform env select $1
terraform env select $1 -no-color

echo Executing operation: terraform destroy
terraform destroy -force -no-color

echo Executing operation: terraform env select default
terraform env select default -no-color

echo Executing operation: terraform env delete $1
terraform env delete $1 -no-color