#!/usr/bin/env sh
echo Executing operation: terraform workspace select $1
terraform workspace select $1 -no-color

echo Executing operation: terraform destroy
terraform destroy -force -no-color

echo Executing operation: terraform workspace select default
terraform workspace select default -no-color

echo Executing operation: terraform workspace delete $1
terraform workspace delete $1 -no-color