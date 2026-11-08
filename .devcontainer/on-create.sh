#!/bin/bash

set -eo pipefail

sudo chown "$(id -u):$(id -g)" "$GRADLE_USER_HOME"

sudo apt-get update
sudo apt-get install -y --no-install-recommends shellcheck
