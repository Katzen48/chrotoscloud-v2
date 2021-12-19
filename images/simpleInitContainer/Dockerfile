FROM node:17

COPY package*.json ./
RUN npm instal
COPY main.js .

CMD [ "node", "main.js" ]