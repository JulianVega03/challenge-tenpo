# Tenpo Challenge — REST API

API REST que recibe dos números, aplica un porcentaje dinámico obtenido de un servicio externo y registra el historial de todas las llamadas de forma asíncrona.

**Stack:** Spring Boot 4 · Java 21 · PostgreSQL · Docker

---

## Cómo funciona

### `POST /api/calculate`

Suma `num1` y `num2`, aplica el porcentaje del servicio externo y retorna el resultado.

**Fórmula:** `resultado = (num1 + num2) × (1 + porcentaje / 100)`

```bash
curl -X POST "http://localhost:8080/api/calculate?num1=5&num2=5"
```

```json
{ "num1": 5, "num2": 5, "percentage": 10.0, "result": 11.00 }
```

### `GET /api/history`

Devuelve el historial paginado de todas las llamadas, ordenado de más reciente a más antiguo.

```bash
curl "http://localhost:8080/api/history?page=0&size=10"
```

### Rate limiting

Máximo **3 requests por minuto**. Al superarlo responde `429 Too Many Requests`.

---

## Cómo probarlo

### Opción 1 — Imagen publicada en Docker Hub (recomendado)

La imagen está disponible en [`julianvega03/challenge-tenpo`](https://hub.docker.com/r/julianvega03/challenge-tenpo).

```bash
# 1. Clonar el repositorio (solo se necesita el docker-compose.yml)
git clone https://github.com/JulianVega03/challenge-tenpo.git
cd challenge-tenpo

# 2. Levantar PostgreSQL + aplicación (usa la imagen de Docker Hub)
docker compose up
```

La API queda disponible en `http://localhost:8080`.

### Opción 2 — Build local

```bash
# Levantar con build local de la imagen
docker compose up --build
```

### Opción 3 — Sin Docker (solo la base de datos en contenedor)

```bash
docker compose up db
./gradlew bootRun
```

### Detener

```bash
docker compose down       # conserva los datos
docker compose down -v    # elimina también el volumen de PostgreSQL
```

---

## Swagger UI

Con la aplicación corriendo:

```
http://localhost:8080/swagger-ui.html
```

---

## Tests

```bash
./gradlew test
```

---

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Docker | 24+ |
| Docker Compose | v2+ |
| Java (solo sin Docker) | 21 |

---

Ver [TECHNICAL_DECISIONS.md](./TECHNICAL_DECISIONS.md) para decisiones de arquitectura y diseño.
