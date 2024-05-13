# java-shareit

## Run shareit app

### with docker-compose

1. docker-compose down
2. mvn clean package
3. docker-compose build
4. docker-compose up

### with docker

1. mvn clean package
2. cd to ./shareit-gateway
3. docker build -t gateway_image .
4. docker run --name gateway_container -p 8081:8081 -p 8080:8080 gateway_image
5. cd to ./shareit-server
6. docker build -t server_image .
7. docker run --name server_container -p 9091:9091 -p 9090:9090 server_image
8. docker run -e POSTGRES_DB=shareit -e POSTGRES_PASSWORD=pass -e POSTGRES_USER=user --name db_container -p 6541:6541 postgres:13.7-alpine  
9. clean up:
   - docker rm gateway_container
   - docker rm server_container
   - docker rm db_container


## Remote Debugging ports
1. Gateway: 8081
2. Sever: 9091

