# Decisiones técnicas

## Arquitectura hexagonal (Ports & Adapters)

El proyecto sigue clean architecture con tres capas bien delimitadas:

```
domain/          ← modelos, interfaces de gateway, casos de uso (sin dependencias externas)
applications/    ← configuración de beans, wiring
infrastructure/  ← adaptadores: JPA, HTTP client, controllers, filters
```

El dominio no tiene dependencia de Spring, JPA ni ningún framework. Esto permite testear los casos de uso de forma completamente aislada con un mock del gateway, sin levantar contexto de Spring ni base de datos. El cambio de PostgreSQL a cualquier otra base de datos o del cliente HTTP al proveedor externo real no requiere tocar la lógica de negocio.

---

## Java 21 — Records para modelos de dominio

`Calculation`, `CallHistory`, `PageRequest`, `CallHistoryEvent` y `PercentageProperties` son `record`. Los records proveen inmutabilidad por defecto, `equals`/`hashCode`/`toString` correctos sin Lombok, y comunican claramente la intención de "portador de datos sin comportamiento".

Reduce el uso de Lombok donde Java ya tiene la solución idiomática. En el dominio, la inmutabilidad es especialmente importante para evitar mutaciones accidentales entre capas.

---

## Java 21 — Virtual threads

`spring.threads.virtual.enabled=true` activa virtual threads en Tomcat y en el executor de `@Async`. Los virtual threads son hilos ligeros (Project Loom) gestionados por la JVM que permiten bloquear en I/O sin bloquear un hilo del OS.

El historial se graba de forma asíncrona usando `@Async`. Con virtual threads, este overhead es mínimo: el costo de crear y destruir un virtual thread es órdenes de magnitud menor que un platform thread.

---

## BigDecimal para cálculos numéricos

Los parámetros, el porcentaje y el resultado usan `BigDecimal` con `RoundingMode.HALF_UP` y escala 2.

`double` tiene representación IEEE 754 de punto flotante binario — `0.1 + 0.2` produce `0.30000000000000004`. `BigDecimal` garantiza aritmética decimal exacta, esencial para cualquier dominio numérico.

---

## Registro de historial con eventos de aplicación asíncronos

El filter `HistoryLoggingFilter` captura request/response de toda llamada a `/api/**` y publica un `CallHistoryEvent`. El listener `AsyncCallHistoryListener` lo procesa con `@Async` en un virtual thread, persistiendo en PostgreSQL.

Desacoplar el registro del flujo principal de la request garantiza que si la persistencia del historial falla, el cliente recibe igual su respuesta. El timestamp se captura en el filter (momento real de la request), no en el listener (que puede ejecutar con delay).

---

## Spring Retry con backoff exponencial

El gateway que llama al servicio externo de porcentaje implementa `@Retryable` con 3 intentos y 500ms de backoff, filtrando solo errores de red (`ResourceAccessException`) y errores de servidor (`RestClientResponseException`), no errores de cliente o de validación.

Reintentar ante `Exception.class` es incorrecto — un `NullPointerException` o un error 400 del servicio externo no se resuelve con reintentos. Acotar `retryFor` a errores transitorios evita reintentos inútiles y mejora el tiempo de respuesta en fallos definitivos.

---

## Rate limiting con Bucket4j (token bucket)

Se usa el algoritmo **token bucket** con 3 tokens recargados por minuto. El bucket es global por instancia de la aplicación.

El token bucket permite ráfagas cortas y luego espera la recarga — el modelo más natural para "3 RPM" como límite suave. Para producción con múltiples instancias, el bucket debería persistirse en Redis (Bucket4j tiene soporte nativo), pero en el scope de este challenge la implementación en memoria es suficiente.

---

## Validación de parámetros en la interfaz de contrato

Las constraints de Bean Validation (`@NotNull`, `@DecimalMin`, `@DecimalMax`, `@Min`, `@Max`) viven en las interfaces `CalculationApi` e `HistoryApi`, no en la implementación del controller.

Bean Validation (JSR-380) prohíbe que un método que sobreescribe otro redefina las restricciones de sus parámetros (LSP aplicado a precondiciones). Las interfaces son la fuente de verdad del contrato: definen tanto la documentación Swagger como las reglas de validación.

---

## Documentación Swagger en interfaces separadas

`CalculationApi` e `HistoryApi` concentran todas las anotaciones de OpenAPI (`@Operation`, `@ApiResponse`, `@Parameter`). Los controllers implementan estas interfaces sin ninguna anotación de documentación.

Las anotaciones de Swagger son verbosas y no aportan valor en la clase de implementación. Mantenerlas en interfaces permite leer el controller como código limpio de Spring MVC. Es también el patrón recomendado por springdoc-openapi.

---

## Dockerfile multi-stage con caché de dependencias

El build stage copia primero los descriptores de dependencias (`build.gradle`, `gradle/`) y resuelve las dependencias antes de copiar el código fuente.

Docker invalida el caché de una capa solo cuando su input cambia. Si el código fuente cambia pero `build.gradle` no, la capa de descarga de dependencias se reutiliza. Sin esta separación, cada `docker build` re-descarga todas las dependencias desde Maven Central.

El stage final usa `eclipse-temurin:21-jre-alpine` (no JDK) y corre con un usuario sin privilegios `spring:spring`.

---

## PostgreSQL con Docker y `ddl-auto=update`

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
