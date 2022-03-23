#!/bin/sh
node main.js
cd /workdir/worlds
find . -name "*.zip" -exec unar -q {} \;

find plugins -name "*.zip" -exec unar -q {} \;

envsubst < /etc/config/paper.yml > /workdir/paper.yml