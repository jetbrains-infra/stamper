#!/usr/bin/env sh
echo Executing operation: terraform workspace select $1
/opt/terraform/terraform workspace select $1 -no-color

echo Executing operation: terraform destroy
/opt/terraform/terraform destroy -force -no-color

echo Executing operation: terraform workspace select default
/opt/terraform/terraform workspace select default -no-color

echo Executing operation: terraform workspace delete $1
/opt/terraform/terraform workspace delete $1 -no-color