#!/usr/bin/env sh
/opt/terraform/terraform workspace select $1 -no-color
/opt/terraform/terraform show -no-color