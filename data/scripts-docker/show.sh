#!/usr/bin/env sh
terraform workspace select $1 -no-color
terraform show -no-color