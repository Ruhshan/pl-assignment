# Running The Project

## Running with docker compose

To run the project with docker compose issue the following command in terminal
```shell
docker compose up
```

This will spin up all the application along with it's dependencies.

### Accessing the applications

#### Gateway

| Base Url     | http://localhost:8081/gateway                  |
|--------------|------------------------------------------------|
| Health Check | http://localhost:8081/gateway/actuator/health  |
| Metrics      | http://localhost:8081/gateway/actuator/metrics |  |
| Api doc      |      http://localhost:8081/gateway/v3/api-docs                                          |
| Swagger UI   |     http://localhost:8081/gateway/v3/swagger-ui/index.html                                                                                    |

#### Statistics

| Base Url     | http://localhost:8083/statistics |
|--------------|--------|
| Health Check | http://localhost:8083/statistics/actuator/health |
| Metrics      | http://localhost:8083/statistics/actuator/metrics       |  |
| Api doc      | http://localhost:8083/statistics/v3/api-docs |
| Swagger UI   | http://localhost:8083/statistics/v3/swagger-ui/index.html |

### Registration

| Base Url     | http://localhost:8082/registration                |
|--------------|---------------------------------------------------|
| Health Check | http://localhost:8082/registration/actuator/health  |
| Metrics      | http://localhost:8082/registration/actuator/metrics |  |
