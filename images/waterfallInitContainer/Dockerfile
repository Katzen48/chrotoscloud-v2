FROM node:17

COPY bungeecord-all.jar .
COPY entrypoint.sh /usr/local/bin/

RUN chmod u+x /usr/local/bin/entrypoint.sh

COPY package*.json ./
RUN npm instal
COPY main.js .

ENTRYPOINT ["entrypoint.sh"]