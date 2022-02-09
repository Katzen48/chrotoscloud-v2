#!/bin/sh
node main.js
cd /workdir/worlds
find . -name "*.zip" -exec unar -q {} \;