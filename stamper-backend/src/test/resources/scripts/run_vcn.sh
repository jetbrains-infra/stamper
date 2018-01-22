#!/usr/bin/env bash
nohup /bin/sh -c "echo secret | /usr/bin/vncviewer  -autopass $1"  > /dev/null 2>&1 &