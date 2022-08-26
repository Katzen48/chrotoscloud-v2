#!/bin/sh
set -e

node .
mkdir /workdir/plugins/
cp bungeecord-all.jar /workdir/plugins/bungeecord-all.jar

/usr/local/bin/geoipupdate