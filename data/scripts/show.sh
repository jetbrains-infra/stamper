#!/usr/bin/env bash
terraform env select $1 -no-color
terraform show -no-color