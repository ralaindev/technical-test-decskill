# API de precios — Decskill

Consulta la tarifa aplicable a un producto y cadena en un instante, seleccionando
la de mayor prioridad.

**Stack:** Java 21, Spring Boot 4.1.1, H2, Flyway, Spring Data JPA, MapStruct y OpenAPI Generator.

## Ejecución

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd clean test
.\mvnw.cmd clean verify
```

En Linux/macOS, usar `./mvnw`. La aplicación arranca en `http://localhost:8080`.

## API

```http
GET /api/v1/prices?queryDate=2020-06-14T16:00:00%2B02:00&productId=35455&brandId=1
```

Devuelve producto, cadena, tarifa, vigencia, precio y moneda. Los parámetros son
obligatorios y los identificadores positivos. Respuestas: `200`, `400` para
entrada inválida y `404` si no hay tarifa.

Contrato: `openapi/prices-api.yaml`. Maven genera las interfaces y modelos Java.
Swagger UI: `http://localhost:8080/swagger-ui.html`.

## Diseño

- **Hexagonal ligera:** REST → caso de uso → servicio → puerto de repositorio →
  adapter JPA. El servicio usa `@Transactional(readOnly = true)`.
- **Fechas:** `OffsetDateTime` en todas las capas, con límites inclusivos y
  comparación por instante. La respuesta conserva el offset almacenado.
- **Persistencia:** Flyway crea y carga H2 en memoria. Las fechas usan
  `TIMESTAMP WITH TIME ZONE`, Hibernate `NATIVE` y `ddl-auto=validate`.
- **Consulta:** JPQL explícita por legibilidad frente a una derived query larga
  y portabilidad frente a SQL nativo. Filtra marca, producto y vigencia, ordena
  por `priority DESC` y limita en base de datos a una fila con `PageRequest.of(0, 1)`.
- **Integridad:** `CHECK (start_date <= end_date)`. Un test valida que los datos
  de Flyway no tengan periodos solapados —incluidos extremos compartidos— para
  la misma marca, producto y prioridad. Se permite reutilizar prioridades en
  periodos separados; no hay desempates arbitrarios.

## Tests

- **Unitarios:** servicio con puerto mockeado; resultado, ausencia y conservación del offset.
- **Integración JPA:** filtros, prioridad, límites temporales, offsets e integridad de datos.
- **Slice MVC:** parsing, validaciones, respuesta y errores con el caso de uso mockeado.
- **E2E:** aplicación completa hasta H2, incluidos los cinco casos del enunciado.
