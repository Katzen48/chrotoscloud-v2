FROM node:18-bullseye

RUN apt-get update && apt-get upgrade -y --with-new-pkgs && apt-get install -y ca-certificates gettext-base unar

COPY geoipupdate /opt/usr/local/bin/
RUN cd /opt/ && \
    find . -name "*.tar.gz" -exec unar -q {} \; && \
    cp geoipupdate /usr/local/bin/ && \
    cp GeoIP.conf /usr/local/etc/

COPY package*.json ./
RUN npm install
COPY main.js .
COPY download.js .

COPY resolveMaven.js .
COPY pluginDownloader.js .
COPY worldDownloader.js .
COPY configurationDownloader.js .

CMD [ "node", "main.js" ]