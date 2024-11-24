# HTTP Forward Proxy Using Spring Boot

This application showcases a simple example of HTTP Forward Proxy using Spring Boot 3

## Features

- HTTP Forward Proxy for GET,POST,PUT and PATCH


## Build
### Using Maven

- Use mvn package to build the module into jar file
```shell
mvn clean package
```
- The following command should be used to run the Java application
```shell
java -jar <path to jar that is build from maven>
```

-The http proxy server will be available in port 8080

### Using Docker Compose
- Install docker and docker compose
- Run docker compose
```shell
docker compose -f docker-compose.yaml up
```
- The http proxy server will be available in port 7070

## Feedback

For feedback, please raise issues in the issue section of the repository. Enjoy!!.

