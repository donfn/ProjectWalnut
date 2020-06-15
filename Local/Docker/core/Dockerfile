FROM node
WORKDIR /usr/src/app
COPY package*.json ./
COPY . .
RUN npm i

EXPOSE 8080
CMD [ "node", "main.js" ]