## Promotion analysis service

### Технологии:
* Spring Boot (ver. 3.1.4)
* Spring Web
* Spring Data JPA (Hibernate)
* PostgreSQL
* Maven
* Liquibase
* Validation (Hibernate Validator)
* Open API (Swagger)
* Lombok
* Spring Test (JUnit 5, Mockito)
* Testcontainers
* Docker + Docker Compose

**Запуск сервиса в Docker-контейнере:**
- собрать проект с помощью Maven;
- выполнить команду `docker compose up` (вместе с сервисом будет запущен контейнер с базой данных).

Swagger UI доступен по адресу:
```
http://localhost:8084/swagger-ui/index.html
```
Скрипты с данными находятся в каталоге тестов:
```
/resources/sql/test/...
```
Признак промо заполняется с помощью вызова эндпоинта:
```
PUT 'http://localhost:8084/api/v1/analysis/actuals/promo'
```
