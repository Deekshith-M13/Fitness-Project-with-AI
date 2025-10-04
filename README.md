# Fitness-Project-with-AI

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.x-red)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A microservices-based AI Activity Recommendation System built with Spring Boot and RabbitMQ for asynchronous messaging. This project powers personalized fitness recommendations by integrating user data, activity tracking, and AI-driven insights via Google Gemini API.

## Features

- **Distributed Microservices Architecture**: Modular services for User management (PostgreSQL), Activity tracking (MongoDB), and AI Recommendations with seamless asynchronous communication via RabbitMQ.
- **Security & Scalability**: JWT-based authentication for hardened endpoints, centralized traffic routing through API Gateway, and dynamic configuration management with Eureka Service Discovery and Config Server.
- **Observability & Reliability**: Structured logging, monitoring hooks (e.g., Actuator endpoints), and tools for debugging inter-service failures to ensure robust operations.
- **Quality Assurance**: Comprehensive unit and integration testing, payload validation, and OpenAPI/Swagger contracts for consistent API interactions.
- **AI Integration**: Leverages Google Gemini API for intelligent activity recommendations based on user profiles and historical data.

*(Add your architecture diagram or service interaction screenshot here for a visual overview.)*

## Architecture Overview

This project follows a microservices pattern with the following core components:

| Service          | Port       | Database/Tech                  | Purpose |
|------------------|------------|--------------------------------|---------|
| **Eureka Server** | 9000      | N/A                            | Service discovery and registration. |
| **Config Server** | 8888      | Native (classpath:/config)     | Centralized configuration management. |
| **User Service** | 8080      | PostgreSQL                     | Handles user registration, profiles, and authentication. |
| **Activity Service** | 8081  | MongoDB                        | Manages fitness activities, tracking, and logs. |
| **AI Service**   | 8082      | MongoDB                        | Generates AI-powered recommendations using Google Gemini. |
| **API Gateway**  | 2000      | N/A (Spring Cloud Gateway)     | Routes requests and enforces security (JWT/OAuth2). |
| **React Frontend** | 5173   | N/A                            | User interface for interacting with the system. |

Services communicate asynchronously via RabbitMQ for events like activity tracking. All services register with Eureka for load balancing.



## Prerequisites

- **Java JDK 21** or higher (Spring Boot 3.x compatible).
- **Maven 3.x** for building.
- **Docker** for containerization and running databases/services.
- **Postman** or similar for API testing.
- **Databases**:
  - PostgreSQL (e.g., via Docker: `docker run --name postgres-db -e POSTGRES_PASSWORD=123 -p 5432:5432 -d postgres`).
  - MongoDB (e.g., via Docker: `docker run --name mongo-db -p 27017:27017 -d mongo`).
- **RabbitMQ** (e.g., via Docker: `docker run --name rabbitmq -p 5672:5672 -p 15672:15672 -d rabbitmq:3-management`).
- **Google Gemini API**: Set `GEMINI_API_URL` and `GEMINI_API_KEY` environment variables.

For OAuth2/JWT (mentioned in Gateway config), ensure Keycloak or similar is running on `localhost:8181` with realm `fitness-oauth2`.

## Installation & Running

1. **Clone the Repository**:
   ```
   git clone https://github.com/Deekshith-M13/Fitness-Project-with-AI.git
   cd Fitness-Project-with-AI
   ```

2. **Set Up Databases & Dependencies**:
   - Start PostgreSQL, MongoDB, and RabbitMQ using Docker (as above).
   - (Optional) Pull and run all services via Docker Compose if you have a `docker-compose.yml` (create one based on the configs below).

3. **Build the Project**:
   ```
   mvn clean install
   ```

4. **Run Services** (in separate terminals for each, or use Docker):
   - **Eureka Server**: `cd eureka-server && mvn spring-boot:run`
   - **Config Server**: `cd config-server && mvn spring-boot:run`
   - **User Service**: `cd user-service && mvn spring-boot:run`
   - **Activity Service**: `cd activity-service && mvn spring-boot:run`
   - **AI Service**: `cd ai-service && mvn spring-boot:run` (ensure Gemini env vars are set).
   - **API Gateway**: `cd api-gateway && mvn spring-boot:run`
   - **React Frontend**: `cd frontend && npm install && npm run dev`

   Alternatively, build fat JARs and run: `java -jar target/*.jar`.

5. **Verify Setup**:
   - Access Eureka Dashboard: [http://localhost:9000](http://localhost:9000)
   - Test API Gateway endpoint: [http://localhost:2000/api/user/**](http://localhost:2000) (replace with actual paths).
  
     +-----------------+
                       |  React Frontend |
                       |      (5173)     |
                       +-----------------+
                              |
                              v
                       +-----------------+
                       |   API Gateway   |
                       |      (2000)     |
                       +-----------------+
                      /     |     \
                     /      |      \
                    v       v       v
     +----------------+  +----------------+  
     |  User Service  |  | Activity Serv. |  
     |   (8080)       |  |    (8081)      |  
     | PostgreSQL     |  |   MongoDB      |  
     +-------+--------+  +----------------+  
             |                    |              
             |                    v              
             |              +-----------------+  
             |              |    RabbitMQ     |  
             |              |  (Async Events) |  
             |              +-----------------+  
             |                       |              
             |                       v              
             |              +----------------+  
             |              |   AI Service   |  
             |              |    (8082)      |  
             |              |   MongoDB      |  
             |              +----------------+  
             |                    ^
             |                    |
    +----------------+  +----------------+  
    |  Config Server |  | Eureka Server  |<-- All microservices register here
    |     (8888)     |  |      (9000)    |
    | (Properties to:|  +----------------+
    |  User/Act/AI/GW|           ^
    +----------------+           |
             ^                   |
             |                   |
             +-------------------+


## Configuration

Configurations are managed via Config Server. Key `application.properties` snippets for each service:

### Eureka Server
```properties
spring.application.name=eureka
server.port=9000
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

### Config Server
```properties
spring.application.name=config-server
spring.profiles.active=native
spring.cloud.config.server.native.search-locations=classpath:/config
server.port=8888
```

### User Service
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fitness_user_db
spring.datasource.username=postgres
spring.datasource.password=123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
eureka.client.serviceUrl.defaultZone=http://localhost:9000/eureka/
eureka.instance.prefer-ip-address=true
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```

### Activity Service
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/fitnessactivity
spring.data.mongodb.database=fitnessactivity
server.port=8081
eureka.client.serviceUrl.defaultZone=http://localhost:9000/eureka/
eureka.instance.prefer-ip-address=true
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
spring.rabbitmq.exchange.name=fitness.exchange
spring.rabbitmq.queue.name=activity.queue
spring.rabbitmq.routing.key=activity.tracking
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### AI Service
```properties
server.port=8082
spring.data.mongodb.uri=mongodb://localhost:27017/fitness-recommendation
spring.data.mongodb.database=fitness-recommendation
eureka.client.serviceUrl.defaultZone=http://localhost:9000/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true
spring.rabbitmq.exchange.name=fitness.exchange
spring.rabbitmq.queue.name=activity.queue
spring.rabbitmq.routing.key=activity.tracking
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
gemini.api.url=${GEMINI_API_URL}
gemini.api.key=${GEMINI_API_KEY}
```

### API Gateway
```properties
server.port=2000
eureka.client.service-url.defaultZone=http://localhost:9000/eureka/
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/certs
spring.cloud.gateway.routes[0].id=USER-SERVICE
spring.cloud.gateway.routes[0].uri=lb://USER-SERVICE
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/user/**
spring.cloud.gateway.routes[1].id=ACTIVITY-SERVICE
spring.cloud.gateway.routes[1].uri=lb://ACTIVITY-SERVICE
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/activities/**
spring.cloud.gateway.routes[2].id=AI-SERVICE
spring.cloud.gateway.routes[2].uri=lb://AI-SERVICE
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/recommendation/**
```

For full configs, refer to the respective service directories.

## Usage

- **Register a User**: `POST http://localhost:2000/api/user/register` (via Postman with JSON payload).
- **Log an Activity**: `POST http://localhost:2000/api/activities/log` – Triggers RabbitMQ event to AI Service.
- **Get Recommendations**: `GET http://localhost:2000/api/recommendation/user/{id}` – Uses Gemini for personalized suggestions.
- Access the React frontend at [http://localhost:5173](http://localhost:5173) to interact visually.

Example curl for activity log:
```bash
curl -X POST http://localhost:2000/api/activities/log \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{"userId": 1, "activity": "Running", "duration": 30}'
```

<img width="923" height="480" alt="Screenshot 2025-10-04 160341" src="https://github.com/user-attachments/assets/2a4ec80e-ff24-4f99-9e93-1a4958242864" />
<img width="951" height="481" alt="Screenshot 2025-10-04 160547" src="https://github.com/user-attachments/assets/883edd94-f59f-421d-836b-1453eb30242f" />
<img width="947" height="487" alt="Screenshot 2025-10-04 160648" src="https://github.com/user-attachments/assets/bd6903f2-1e3f-4d11-be1a-9f71817c0c6d" />
<img width="905" height="350" alt="Screenshot 2025-10-04 160731" src="https://github.com/user-attachments/assets/240ba1ed-3a16-4f79-99a0-6e5bb9cad4f4" />
<img width="925" height="336" alt="Screenshot 2025-10-04 160817" src="https://github.com/user-attachments/assets/2d0b51f9-ba59-4a8d-aaae-5d7cd76e277a" />
<img width="911" height="446" alt="Screenshot 2025-10-04 160956" src="https://github.com/user-attachments/assets/b12fbe6d-3063-41b2-823e-50d021aa92c7" />
<img width="778" height="489" alt="Screenshot 2025-10-04 161041" src="https://github.com/user-attachments/assets/a844c148-8171-4946-8cd6-e692de688ac4" />
<img width="776" height="326" alt="Screenshot 2025-10-04 161155" src="https://github.com/user-attachments/assets/24aab6cf-e3ed-4d9f-bc8f-7589a3c6c5de" />
<img width="773" height="389" alt="Screenshot 2025-10-04 161231" src="https://github.com/user-attachments/assets/cdac83e3-98cd-46ce-8735-1578ba3817c9" />










## Testing

- **Unit Tests**: Run `mvn test` in each service directory.
- **Integration Tests**: Use Testcontainers for DB mocks; run `mvn verify`.
- **API Contracts**: Validate with Swagger UI (if enabled) at `/swagger-ui.html` per service.

## Contributing

Contributions welcome! Fork the repo, create a feature branch (`git checkout -b feature/amazing-feature`), and submit a Pull Request. Ensure tests pass and follow the style guidelines.

1. Deekshith-M13 (@Deekshith-M13 on GitHub)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. (Create a `LICENSE` file with MIT text if not already present.)
