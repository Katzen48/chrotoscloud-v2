FROM node:18-bullseye

RUN apt-get update && apt-get upgrade -y --with-new-pkgs && apt-get install -y ca-certificates gettext-base

COPY package*.json ./
RUN npm install
COPY main.js .
COPY download.js .

COPY resolveMaven.js .
COPY pluginDownloader.js .
COPY worldDownloader.js .
COPY configurationDownloader.js .

CMD [ "node", "main.js" ]