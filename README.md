# Mini Doodle

Simple meeting scheduling service built with:

- Java 21
- Spring Boot
- PostgreSQL
- Docker Compose


## Monitoring

The service exposes Spring Boot Actuator endpoints.

Health:
http://localhost:8080/actuator/health

Metrics:
http://localhost:8080/actuator/metrics

Prometheus:
http://localhost:8080/actuator/prometheus

Custom metrics:
- meetings.created
- slots.created
- meeting.creation.time

## Run

```bash
mvn clean package
docker compose up --build
