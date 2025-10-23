FROM node:18-alpine AS deps
WORKDIR /app

COPY package.json package-lock.json ./

RUN npm config set strict-ssl false
RUN npm install

FROM node:18-alpine
WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules

COPY server.js .
COPY src ./src
COPY package.json .

EXPOSE 3000

CMD [ "node", "server.js" ]