#!/bin/sh
set -e

node main.js
cd /workdir/worlds
find . -name "*.zip" -exec unar -q {} \;

cd ../plugins
find . -name "*.zip" -exec unar -q -d {} \;

envsubst < /etc/config/paper.yml > /workdir/paper.yml

/usr/local/bin/geoipupdate