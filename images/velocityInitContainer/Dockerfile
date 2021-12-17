FROM node:17

COPY velocity-all .
COPY entrypoint.sh .

RUN chmod u+x entrypoint.sh

COPY package*.json ./
RUN npm instal
COPY main.js .

ENTRYPOINT entrypoint.sh