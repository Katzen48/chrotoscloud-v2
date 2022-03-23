#!/bin/sh
node main.js
cd /workdir/worlds
find . -name "*.zip" -exec unar -q {} \;

cd ../plugins
find . -name "*.zip" -exec unar -q {} \;

envsubst < /etc/config/paper.yml > /workdir/paper.yml