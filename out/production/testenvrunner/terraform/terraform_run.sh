#!/usr/bin/env bash

MY_PATH="`dirname \"$0\"`" # relative
MY_PATH="`(cd \"${MY_PATH}\" && pwd)`" # absolutized and normalized
if [ -z "$MY_PATH" ]; then
    # error; for some reason, the path is not accessible
    # to the script (e.g. permissions re-evaled after suid)
    echo ERROR: Path to terraform_run script cannot been resolved
    exit 1 # fail
fi
#cd ${MY_PATH}
cd $1
terraform plan -no-color $1
terraform apply -no-color
