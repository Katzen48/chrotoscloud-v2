#!/bin/sh
set -e

node .
mkdir -p /workdir/plugins/
cp velocity-all.jar /workdir/plugins/velocity-all.jar

envsubst < /etc/config/velocity.toml > /workdir/velocity.toml

/usr/local/bin/geoipupdate