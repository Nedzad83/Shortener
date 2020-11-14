FROM openjdk:11
COPY /target/shortener-0.0.1-SNAPSHOT.jar shortener-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","shortener-0.0.1-SNAPSHOT.jar"]