# HAPI FHIR Client

A Spring Boot application that demonstrates integration with FHIR servers using HAPI FHIR client library. This client provides RESTful access to FHIR resources including Patient, Immunization, Procedure, and AllergyIntolerance data.

## About

This application implements the HAPI FHIR Generic (Fluent) client, which offers a flexible and easy-to-use approach for building FHIR REST invocations. The client is configured to handle various FHIR resources and provides REST endpoints for interacting with a FHIR server.

## Tech Stack

- Java 21
- Spring Boot
- HAPI FHIR Client Library

## Features

- FHIR Resource Support:
  - Patient Management
  - Immunization Records
  - Procedure Records
  - Allergy Intolerance Records
- RESTful endpoints for FHIR operations

## Running the Application

1. Prerequisites:
   - Java 21 or higher
   - Maven
   - A HAPI FHIR server needs to be up and running.
    [Refer to this link on how to set it up](https://github.com/hapifhir/hapi-fhir-jpaserver-starter)
    
    Important: Update the fhir.server.base.url in application.properties with the local fhir server's base url.

2. Clone the repository:
   ```bash
   git clone [repository-url]
   cd hapi-fhir-client
   ```

3. Build the application:
   ```bash
   ./mvnw clean install
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on the default port (8088) unless configured otherwise in `application.properties`.

## Configuration

The application uses Spring Boot's configuration system. Key configurations can be found in:
- `src/main/resources/application.properties` - Application configuration
- `src/main/java/dev/sagar/hapi_fhir_client/config/FHIRConfiguration.java` - FHIR client configuration
