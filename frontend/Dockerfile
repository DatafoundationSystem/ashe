FROM node:18 as build-stage
WORKDIR /frontend
COPY package.json .
RUN npm install
COPY . .
RUN npm run build
RUN npm install -g serve
CMD serve -s build
