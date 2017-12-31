#!/usr/bin/env bash
nohup /bin/sh -c "echo secret | /usr/bin/vncviewer  -autopass localhost:32924"  > /dev/null 2>&1 &