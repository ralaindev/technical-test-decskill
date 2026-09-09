# API de precios — Decskill

API para consultar la tarifa aplicable a un producto y una cadena en una fecha determinada, seleccionando la de mayor prioridad.

**Stack:** Java 21, Spring Boot 4.1.1, Spring Data JPA, H2, Flyway, MapStruct y OpenAPI Generator.

## Ejecución

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd clean test
.\mvnw.cmd clean verify
```

La aplicación arranca en:

```text
http://localhost:8080
```

## API

```http
GET /api/v1/prices?queryDate=2020-06-14T16:00:00%2B02:00&productId=35455&brandId=1
```

Contrato OpenAPI:

```text
openapi/prices-api.yaml
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Diseño

- Arquitectura hexagonal ligera.
- Flyway para esquema y datos iniciales.
- JPQL explícita para obtener la tarifa aplicable.

## Tests

- **Unitarios:** `GetPriceServiceTest`.
- **Integración JPA:** `SpringDataPriceRepositoryTest`.
- **E2E:** `PriceApiE2ETest`, incluyendo los cinco casos del enunciado.