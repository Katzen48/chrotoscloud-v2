FROM node:19-bullseye

RUN apt-get update && apt-get upgrade -y --with-new-pkgs && apt-get install -y ca-certificates gettext-base unar

COPY geoipupdate/* /opt/
RUN cd /opt/ && \
    find . -name "*.tar.gz" -exec unar {} \; && \
    cp */geoipupdate /usr/local/bin/

COPY package*.json ./
RUN npm install
COPY main.js .
COPY download.js .

COPY resolveMaven.js .
COPY pluginDownloader.js .
COPY worldDownloader.js .
COPY configurationDownloader.js .

CMD [ "node", "main.js" ]