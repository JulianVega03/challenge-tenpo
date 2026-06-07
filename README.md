# Tenpo Challenge — REST API con Spring Boot 4 + Java 21

API REST que recibe dos números, aplica un porcentaje dinámico obtenido de un servicio externo y registra el historial de todas las llamadas de forma asíncrona.

---

## Tabla de contenidos

- [Requisitos previos](#requisitos-previos)
- [Ejecución local](#ejecución-local)
- [Endpoints de la API](#endpoints-de-la-api)
- [Documentación interactiva (Swagger)](#documentación-interactiva-swagger)
- [Tests](#tests)
- [Decisiones técnicas](#decisiones-técnicas)
- [Estructura del proyecto](#estructura-del-proyecto)

---

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Docker | 24+ |
| Docker Compose | v2+ |
| Java (solo si se ejecuta sin Docker) | 21 |

---

## Ejecución local

### Con Docker Compose (recomendado)

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd challenge-tenpo

# 2. Configurar credenciales (opcional — ya tienen valores por defecto)
cp .env.example .env

# 3. Levantar base de datos y aplicación
docker compose up --build
```

La API queda disponible en `http://localhost:8080`.  
Swagger UI disponible en `http://localhost:8080/swagger-ui.html`.

Para detener:
```bash
docker compose down          # conserva los datos
docker compose down -v       # elimina también el volumen de PostgreSQL
```

### Sin Docker (solo la base de datos en Docker)

```bash
# Levantar solo PostgreSQL
docker compose up db

# Ejecutar la aplicación con Gradle
./gradlew bootRun
```

---

## Endpoints de la API

### POST `/api/calculate`

Suma `num1` y `num2`, luego aplica el porcentaje obtenido del servicio externo.

**Fórmula:** `resultado = (num1 + num2) × (1 + porcentaje / 100)`

**Parámetros de query:**

| Parámetro | Tipo       | Requerido | Rango           |
|-----------|------------|-----------|-----------------|
| `num1`    | BigDecimal | Sí        | -1000000 a 1000000 |
| `num2`    | BigDecimal | Sí        | -1000000 a 1000000 |

**Ejemplo:**
```bash
curl -X POST "http://localhost:8080/api/calculate?num1=5&num2=5"
```

**Respuesta exitosa (`200 OK`):**
```json
{
  "num1": 5,
  "num2": 5,
  "percentage": 10.0,
  "result": 11.00
}
```

**Errores posibles:**

| Código | Descripción |
|--------|-------------|
| `400`  | Parámetro faltante, tipo inválido o fuera de rango |
| `429`  | Rate limit excedido (máximo 3 RPM) |
| `503`  | Servicio externo de porcentaje no disponible después de 3 reintentos |

---

### GET `/api/history`

Devuelve el historial paginado de todas las llamadas realizadas a la API, ordenado de más reciente a más antiguo.

**Parámetros de query:**

| Parámetro | Tipo | Default | Rango  |
|-----------|------|---------|--------|
| `page`    | int  | `0`     | ≥ 0    |
| `size`    | int  | `10`    | 1 – 100 |

**Ejemplo:**
```bash
curl "http://localhost:8080/api/history?page=0&size=5"
```

**Respuesta exitosa (`200 OK`):**
```json
{
  "content": [
    {
      "id": "a1b2c3d4-...",
      "timestamp": "2024-06-06T18:55:03",
      "endpoint": "/api/calculate",
      "method": "POST",
      "parameters": "num1=5&num2=5",
      "response": "{\"num1\":5,\"num2\":5,\"percentage\":10.0,\"result\":11.00}",
      "errorMessage": null,
      "httpStatus": 200
    }
  ],
  "totalElements": 42,
  "totalPages": 9,
  "page": 0
}
```

> El historial se registra de forma asíncrona: si una llamada falla con un error 4xx o 5xx, el campo `errorMessage` contiene el cuerpo del error y `response` es `null`, y viceversa.

---

### Rate Limiting

La API acepta un máximo de **3 requests por minuto** (por instancia). Al superarlo:

```http
HTTP/1.1 429 Too Many Requests
Content-Type: application/json

{"error": "Rate limit exceeded", "message": "Maximum 3 requests per minute allowed"}
```

---

## Documentación interactiva (Swagger)

Con la aplicación corriendo, acceder a:

```
http://localhost:8080/swagger-ui.html
```

El contrato OpenAPI en formato JSON está disponible en:

```
http://localhost:8080/v3/api-docs
```

---

## Tests

```bash
./gradlew test
```

Los tests cubren:

- `CalculateWithPercentageUseCaseTest` — lógica de cálculo con BigDecimal, manejo de cero, negativos, decimales y propagación de errores del servicio externo.
- `RateLimitFilterTest` — verificación del filtro de rate limiting: permite requests dentro del límite, bloquea con 429 al excederlo y bypasea rutas no-API.

---

## Decisiones técnicas

### Arquitectura hexagonal (Ports & Adapters)

El proyecto sigue clean architecture con tres capas bien delimitadas:

```
domain/          ← modelos, interfaces de gateway, casos de uso (sin dependencias externas)
applications/    ← configuración de beans, wiring
infrastructure/  ← adaptadores: JPA, HTTP client, controllers, filters
```

**Motivación:** el dominio no tiene dependencia de Spring, JPA ni ningún framework. Esto permite testear los casos de uso de forma completamente aislada con un mock del gateway, sin levantar contexto de Spring ni base de datos. El cambio de PostgreSQL a cualquier otra base de datos o del cliente HTTP al proveedor externo real no requiere tocar la lógica de negocio.

---

### Java 21 — Records para modelos de dominio

`Calculation`, `CallHistory`, `PageRequest`, `CallHistoryEvent` y `PercentageProperties` son `record`. Los records proveen inmutabilidad por defecto, `equals`/`hashCode`/`toString` correctos sin Lombok, y comunican claramente la intención de "portador de datos sin comportamiento".

**Motivación:** reducir el uso de Lombok donde Java ya tiene la solución idiomática. En el dominio, la inmutabilidad es especialmente importante para evitar mutaciones accidentales entre capas.

---

### Java 21 — Virtual threads

`spring.threads.virtual.enabled=true` activa virtual threads en Tomcat y en el executor de `@Async`. Los virtual threads son hilos ligeros (Project Loom) gestionados por la JVM, que permiten bloquear en I/O sin bloquear un hilo del OS. Con ellos, una solicitud que espera a la base de datos o al servicio externo no consume un hilo del pool del OS.

**Motivación:** el historial se graba de forma asíncrona (para no afectar la latencia del endpoint principal) usando `@Async`. Con virtual threads, este overhead es mínimo: el costo de crear y destruir un virtual thread es órdenes de magnitud menor que un platform thread.

---

### BigDecimal para cálculos numéricos

Los parámetros, el porcentaje y el resultado usan `BigDecimal` con `RoundingMode.HALF_UP` y escala 2.

**Motivación:** `double` tiene representación IEEE 754 de punto flotante binario. `0.1 + 0.2` en `double` produce `0.30000000000000004`. En una API de cálculo esto es un bug silencioso. `BigDecimal` garantiza aritmética decimal exacta, esencial para cualquier dominio numérico.

---

### Registro de historial con eventos de aplicación asíncronos

El filter `HistoryLoggingFilter` captura request/response de toda llamada a `/api/**` y publica un `CallHistoryEvent`. El listener `AsyncCallHistoryListener` lo procesa con `@Async` en un virtual thread, persistiendo en PostgreSQL.

**Motivación:** desacoplar el registro del flujo principal de la request. Si la persistencia del historial falla (base de datos lenta, error transitorio), el cliente recibe igual su respuesta. El timestamp se captura en el filter (momento real de la request), no en el listener (que puede ejecutar con delay).

---

### Spring Retry con backoff exponencial

El gateway que llama al servicio externo de porcentaje implementa `@Retryable` con 3 intentos y 500ms de backoff, filtrando solo errores de red (`ResourceAccessException`) y errores de servidor (`RestClientResponseException`), no errores de cliente o de validación.

**Motivación:** reintentar ante `Exception.class` es incorrecto — un `NullPointerException` o un error 400 del servicio externo no se resuelve con reintentos. Acotar `retryFor` a errores transitorios de red/servidor evita reintentos inútiles y mejora el tiempo de respuesta en casos donde el fallo es definitivo.

---

### Rate limiting con Bucket4j (token bucket)

Se usa el algoritmo **token bucket** con 3 tokens recargados por minuto. El bucket es global por instancia de la aplicación.

**Motivación:** el token bucket permite ráfagas cortas (consumir varios tokens seguidos) y luego esperar la recarga. Es el modelo más natural para "3 RPM" como límite suave. Para producción con múltiples instancias, el bucket debería persistirse en Redis (Bucket4j tiene soporte nativo para ello), pero en el scope de este challenge la implementación en memoria es suficiente.

---

### Validación de parámetros en la interfaz de contrato

Las constraints de Bean Validation (`@NotNull`, `@DecimalMin`, `@DecimalMax`, `@Min`, `@Max`) viven en las interfaces `CalculationApi` e `HistoryApi`, no en la implementación del controller.

**Motivación:** Bean Validation (JSR-380) prohíbe que un método que sobreescribe otro redefina las restricciones de sus parámetros (Principio de Sustitución de Liskov aplicado a precondiciones). Las interfaces son la fuente de verdad del contrato: definen tanto la documentación Swagger como las reglas de validación. El controller solo implementa sin repetir.

---

### Documentación Swagger en interfaces separadas

`CalculationApi` e `HistoryApi` concentran todas las anotaciones de OpenAPI (`@Operation`, `@ApiResponse`, `@Parameter`). Los controllers implementan estas interfaces sin ninguna anotación de documentación.

**Motivación:** las anotaciones de Swagger son verbosas y no aportan valor en la clase de implementación. Mantenerlas en interfaces permite leer el controller como código limpio de Spring MVC, sin ruido de documentación. Es también el patrón recomendado por springdoc-openapi.

---

### Dockerfile multi-stage con caché de dependencias

El build stage copia primero los descriptores de dependencias (`build.gradle`, `gradle/`) y resuelve las dependencias antes de copiar el código fuente.

**Motivación:** Docker invalida el caché de una capa solo cuando su input cambia. Si el código fuente cambia pero `build.gradle` no, la capa de descarga de dependencias (que es la más lenta) se reutiliza. Sin esta separación, cada `docker build` re-descarga todas las dependencias desde Maven Central.

El stage final usa `eclipse-temurin:21-jre-alpine` (no JDK) y corre con un usuario sin privilegios `spring:spring`.

---

### PostgreSQL con Docker y `ddl-auto=update`

La base de datos corre en un contenedor Docker con un healthcheck que garantiza que PostgreSQL acepte conexiones antes de arrancar la aplicación (`depends_on: condition: service_healthy`).

Se usa `ddl-auto=update` para simplificar el setup inicial del challenge. En un entorno productivo se reemplazaría por Flyway o Liquibase para control de versiones del schema.

---

## Estructura del proyecto

```
challenge-tenpo/
├── deployment/
│   └── Dockerfile
├── src/
│   ├── main/java/com/challenge/challengetenpo/
│   │   ├── applications/config/          # wiring de casos de uso
│   │   ├── domain/
│   │   │   ├── model/                    # records de dominio + excepciones
│   │   │   │   └── gateway/              # interfaces (ports)
│   │   │   └── usecase/                  # lógica de negocio pura
│   │   └── infrastructure/
│   │       ├── adapters/
│   │       │   ├── externalpercentage/   # cliente HTTP + retry
│   │       │   └── jparepository/        # adaptador JPA
│   │       └── entrypoints/apirest/
│   │           ├── filter/               # rate limit + logging
│   │           ├── handler/              # manejo global de errores
│   │           ├── listener/             # persistencia asíncrona del historial
│   │           ├── swagger/              # interfaces con documentación OpenAPI
│   │           └── mock/                 # mock del servicio externo
│   └── resources/
│       └── application.properties
├── docker-compose.yml
├── .dockerignore
└── .env.example
```
