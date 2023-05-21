FROM node:16 AS build
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .
RUN npm run build
VOLUME ["/app/dist"]

FROM nginx
RUN rm -rf /etc/nginx/conf.d
COPY ./groot.conf /etc/nginx/conf.d/
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 3000
CMD ["nginx", "-g", "deamon off;"]