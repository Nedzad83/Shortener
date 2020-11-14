# Shortener

This is an API for Url shortener service like tiny url. 

Url shortener is service that converts long urls into short aliases to save space when sharing urls in messages, presentations etc.

## Solution Overview

Component View of the Backend Short Url Service:
* UrlController - Manages all the Rest API endpoints of the application (Creation route, Deletion route and Redirect route)
* ManagementService - Manages the core functionalities such as creation of short url and deletion by id
* RedirectService - Manages the core functionalities such as redirection
* ManagementRepository - Manages all the data with MySQL DB
* RedisRepository - Manages Redis

RabbitMQ is used for transferring messages between services. MySQL is used as persistence layer.
After creating or deleting short URL, the information must be sent to RabbitMQ/Kafka.The Redis cache server is introduced as a middle layer to improve the performance of the application.

## Technology Stack:
* [Spring Boot](http://spring.io/projects/spring-boot) for creating the REST Web Services.
* [Redis](https://redis.io/) used as a distributed in-memory data store. Application is mostly served by the cache server for high performance.
* [Maven](https://maven.apache.org/) for building the projects
* [Junit](https://junit.org/) for writing unit, integration tests for the application. 
* [MySQL] is used as persistence layer.
* [Docker](https://www.docker.com/) for building and managing the application as images. The build and deploy phases are completed part of Docker.

# How to use 
+ With Docker and docker-compose: 

## Build & Run Steps
#### Download
```sh
$ git clone https://github.com/Nedzad83/Shortener.git
$ cd Shortener 
```

#### Build
```sh
$ Navigate to downloaded directory
$ mvn clean install -DskipTests
$ docker-compose build
```
#### Deploy & Run
```sh
$ docker-compose up
```
With the default setup, the application should be available at http://localhost:8080/

#### Creation route (POST) http://localhost:8080/create-short

This method should create short URL based on long URL
Request example:
```
{
  "longUrl": "https://stackoverflow.com"
}
```
Response example:
```
{
    "longUrl": "https://stackoverflow.com",
    "hashIdentification": "77e9147f",
    "id": 1
}
```
#### Redirect route (GET) http://localhost:8080/{hashIdenetification}
Request example:
```
http://localhost:8080/77e9147f
```
Response example:
```
URL already exists
* return 302 http code for existing short URL
```

* Route should return 302 http code for existing short URL.
* Route should return 404 http code for non existing short URL.

Implemented rate limiting on Redirection Service where service allows 10 redirect requests in
period of 120 seconds for specific URL.

* Route will return 429 http code after reaching threshold.
```
You have exceeded the number of attempts for this hashcode! Please try in 2 mins again!
```
#### Deletion route (DEL) http://localhost:8080/{id}
* Remove short URL using id.

Request example:
```
localhost:8080/delete-url/1
```
Response example:
```
 "Message": "Sucessfully deleted url"
```




