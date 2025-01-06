# ACME Bookings!

>>> A reliable room booking program,unlike our other products

## Getting Started

### Prerequisites

To run, build, and extend the application locally, the following tools are required:

- **Docker**: For running the database and APIs.
- **IntelliJ IDEA**: For code development.
- **JDK 17**: Ensure you have JDK 17 installed (IntelliJ can handle this for you).

## Docker

### Dockerfile
The Dockerfile is used to build the Docker image for the application. It consists of two stages:

#### Stage 1: Build the Application
```Dockerfile
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build/app

# Copy the parent POM and app module
COPY pom.xml ./
COPY src ./src/

# Build the app module
RUN mvn clean package -DskipTests
``` 

- Base Image: Uses the Maven image with Eclipse Temurin JDK 17.
- Working Directory: Sets the working directory to /build/app.
- Copy Files: Copies the pom.xml and src directory to the working directory.
- Build: Runs the Maven build command to package the application, skipping tests.

#### Stage 2: Run the Application
```Dockerfile
FROM openjdk:17
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /build/app/target/*.jar /acme-booking.app

# Define the entry point
ENTRYPOINT ["java", "-jar", "/acme-booking.app"]
```

- Base Image: Uses the OpenJDK 17 image.
- Working Directory: Sets the working directory to /app.
- Copy Files: Copies the built JAR file from the build stage to the working directory.
- Entry Point: Defines the entry point to run the application using the java -jar command.

### Deploying with Docker:

The Docker Compose setup deploys the following services:

- Postgres DB: The PostgreSQL database instance.
- Liquibase: A service that automatically runs Liquibase scripts to manage the database schema and initial data.
- Application: The main application.

#### Environment Variables
Create a docker.env file in the root directory with the following content:

```properties
POSTGRES_DB=<db_name>
POSTGRES_USER=<db_user>
POSTGRES_PASSWORD=<db_password>

SPRING_DATASOURCE_URL=jdbc:postgresql://acme_booking_db:5432/<db_name>?serverTimezone=HMT
SPRING_DATASOURCE_USERNAME=<db_user>
SPRING_DATASOURCE_PASSWORD=<db_password>

LIQUIBASE_COMMAND_URL=jdbc:postgresql://acme_booking_db:5432/<db_name>?serverTimezone=HMT
LIQUIBASE_COMMAND_USERNAME=<db_user>
LIQUIBASE_COMMAND_PASSWORD=<db_password>
```

##### Environment Variables Matrix
| Variable Name               | Description                              |
|-----------------------------|------------------------------------------|
| POSTGRES_DB                 | The name of the PostgreSQL database.     |
| POSTGRES_USER               | The username for the PostgreSQL database.|
| POSTGRES_PASSWORD           | The password for the PostgreSQL database.|
| SPRING_DATASOURCE_URL       | The JDBC URL for the Spring datasource.  |
| SPRING_DATASOURCE_USERNAME  | The username for the Spring datasource.  |
| SPRING_DATASOURCE_PASSWORD  | The password for the Spring datasource.  |
| LIQUIBASE_COMMAND_URL       | The JDBC URL for Liquibase.              |
| LIQUIBASE_COMMAND_USERNAME  | The username for Liquibase.              |
| LIQUIBASE_COMMAND_PASSWORD  | The password for Liquibase.              |


By running 
```shell
docker-compose up --build -d
```
all necessary components are started, and the Liquibase service ensures that the database schema is up-to-date before the application starts. The docker.env file contains all the necessary environment variables for configuring these services.

##### Command Attributes:

- `-build`: re-building the images of all services included in docker-compose file.
- `d`: runs the services in detached mode, meaning you can use the same terminal for other tasks.


## Postgres Database

The PostgreSQL database is a critical component of the ACME Bookings application. It stores all the data related to bookings, users, and rooms. 
<br> The database can be configured using environment variables, which allows for flexibility and better security.

### Configuration
The PostgreSQL database can be configured using the following environment variables:

```properties
POSTGRES_DB= The name of the database.
POSTGRES_USER= The username for the database.
POSTGRES_PASSWORD= The password for the database.
```

These environment variables can be set in a .env file or directly in the docker-compose.yml file.
<br> By setting in a .env file, like shown above, we maintain security without compromising sensitive information.


## Running the App

### Secrets Management

Create a `secrets.properties` file in the `src/main/resources` directory with the following keys:

```properties
# Spring Datasource
spring.datasource.url=jdbc:postgresql://<db_url>/<db_name>?serverTimezone=HMT
spring.datasource.username=<db_user>
spring.datasource.password=<db_password>
```

Make sure the properties above correspond correctly to the properties defined in `docker.env` file we set up earlier.

### Build the Application

```shell
mvn clean install
```

### Run the Application locally 

You can run the application using your IDE (IntelliJ IDEA) or from the command line:

```shell
mvn spring-boot:run
```

> Make sure that 
> - docker-compose has successfully run, ensuring our db is correctly set up 
> - the app is not running in docker <br>_(else a different port is going to be needed to avoid conflicts)_

## API Specifications

### Swagger / OpenAPI Spec

The API specifications are available at:

- [Swagger UI](http://localhost:8080/swagger-ui/index.html) (when the application is running)
- As a JSON file generated upon Maven build lifecycle completion, located at `target/booking-api.json`

## Bookings

### Search Bookings
To search for bookings, send a GET request to /api/v1/bookings with the following JSON payload in the request body:

```json

{
"roomName": "Conference Room",
"date": "2025-01-07"
}
```

### Create a Booking
To create a booking, send a POST request to /api/v1/bookings with the following JSON payload:

```json
{
"userEmail": "user@example.com",
"roomName": "Conference Room",
"bookingStartDateTime": "2025-01-07T10:00:00",
"numberOfHours": 2
}
```

### Cancel a Booking
To cancel a booking, send a DELETE request to /api/v1/bookings with the uuid parameter:

```http request
DELETE /api/v1/bookings?uuid=<booking-uuid>
```

## Future Enhancements

- REST API:
  - Implement SSL for secure communication.
  - Add authentication mechanisms. 

- Repository:
  - Opt for query building (more fine-grained) approach, instead of hard-coded queries, for extensibility.
  - Implement a retention policy on past bookings.
  - Enable audit logging for traffic monitoring.
  
- Time:
  - Consider using Zoned Datetime for all time-related data and configure it during deployment for better time zone management.
