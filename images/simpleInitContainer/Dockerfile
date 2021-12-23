FROM node:17

COPY package*.json ./
RUN npm install
COPY main.js .
COPY download.js .
COPY resolveMaven.js .

CMD [ "node", "main.js" ]