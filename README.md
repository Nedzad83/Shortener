# Shortener

This is an API for Url shortener service like tiny url. 

Url shortener is service that converts long urls into short aliases to save space when sharing urls in messages, presentations etc.

Url shortener is an application for URL shortening. An example of such application is
TinyURL (http://tinyurl.com/). The application contains 2 services. The first service is a
Management Service, another service is Redirection Service. RabbitMQ/Kafka will be used
for transferring messages between services.

# Architecture Diagram
![image](https://user-images.githubusercontent.com/4907367/142269024-c4fca3d2-0b87-4276-99de-9f45dfaa0d19.png)

# Management Service
Management Service has RESTful API for creating and deleting URLs. There are two
routes:

###### Creation route
● Route should create short URL based on real URL.
● Short URL must be unique.
● Short URL hash identification must be located in URI path. eg.
http://localhost:8080/uAYC3sOddP
● Hashing algorithm is not important, so it can be any licence free algorithm.
Request example:
{
"realURL": "https://www.nsoft.com/job-application/?job_id=7661"
}
Response example:
{
"id": 3,
"realURL": "https://www.nsoft.com/job-application/?job_id=7661",
"shortURL": "http://localhost:8080/gfjhgESta"
}

###### Deletion route
● Remove short URL using id.
Management service should use MySQL/PostgreSQL for persistence layer.
After creating or deleting short URL, the information must be sent to RabbitMQ/Kafka.

# Redirection Service
The service will find real URL, based on hash part of the short URL and the user will be
redirected to real URL. Redirection accepts information about short URLs through RabbitMQ/
Kafka. In the case of creating short URL on Management Service, the information will be stored
in Redis on Redirection Service, while in the case of deleting short URL, the information will be
deleted from Redis on Redirection Service.

Redirection service has one RESTful API route.
Redirect route
● Route should return 302 http code for existing short URL.
● Route should return 404 http code for non existing short URL.

# Additional implementation

Implemented rate limiting on Redirection Service where service allows 10 redirect requests in
period of 120 seconds for specific URL.
Redirect route
● Route should return 429 http code after reaching threshold.

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
* [MySQL](https://www.mysql.com/) is used as persistence layer.
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




