FROM node:17-bullseye

RUN apt-get update && apt-get install -y gettext-base

COPY package*.json ./
RUN npm install
COPY main.js .
COPY download.js .

COPY resolveMaven.js .
COPY pluginDownloader.js .
COPY worldDownloader.js .

CMD [ "node", "main.js" ]