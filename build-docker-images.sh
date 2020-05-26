#!/usr/bin/env bash

set -eo pipefail

modules=( voting-agenda-service voting-session-service voting-app-service )

for module in "${modules[@]}"; do
    docker build -t "${module}:latest" ${module}
done