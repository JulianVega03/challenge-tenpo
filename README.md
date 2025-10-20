# 🚀 Challenge Backend - API REST en Spring Boot

## Descripción General
Este proyecto consiste en una API REST desarrollada en Spring Boot (Java 21). Su objetivo principal es demostrar habilidades en la integración de servicios externos (mock), la implementación de mecanismos de caché para la resiliencia, y el manejo asíncrono de persistencia de datos con PostgreSQL.

## Funcionalidades Clave
- **Cálculo Resiliente:** Suma de dos números con aplicación de un porcentaje dinámico obtenido de un servicio externo (mock).  
- **Mecanismo de Caché:** El porcentaje del servicio externo se almacena en caché en memoria durante 30 minutos para garantizar la disponibilidad en caso de fallo del servicio proveedor.  
- **Registro Asíncrono de Historial:** Se guarda un registro de todas las interacciones de la API (fecha, endpoint, parámetros, respuesta o error) de forma asíncrona en una base de datos PostgreSQL.  

## 🛠️ Requisitos Previos
Para levantar y ejecutar la aplicación, solo se requiere tener instalado:
- Docker  
- Docker Compose (generalmente incluido con las instalaciones modernas de Docker).  

## ⚙️ Configuración y Ejecución con Docker
La aplicación y la base de datos PostgreSQL se levantan mediante un único comando `docker-compose`.

### 1. Clonar el Repositorio
```bash
git clone https://github.com/JulianVega03/challenge-tenpo.git
cd challenge-tenpo
````

### 2. Configurar Variables de Entorno

Crea un archivo llamado `.env` en la raíz del proyecto ó usar el `.env` disponible en el repositorio.
```env
# Archivo: .env
# Variables para la base de datos
DB_PRIVATE_HOST=db
DB_PORT=5432
DB_NAME=challenge
DB_USER=postgres
DB_PASS=postgres

POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=challenge

# Configuración de cache
CACHE_EXPIRATION_TIME=30
CACHE_EXPIRATION_TIME_UNIT=MINUTES

# URL de servicio externo
EXTERNAL_SERVICE_BASE_URL=http://localhost:8080/mock
EXTERNAL_SERVICE_PERCENTAGE_PATH=/external/percentage

```

### 3. Levantar los Contenedores

Ejecuta Docker Compose iniciar ambos servicios:

```bash
docker-compose up -d
```

### Servicios y Puertos

| Servicio        | Puerto Expuesto | Descripción                      |
| --------------- | --------------- | -------------------------------- |
| PostgreSQL      | 5432            | Base de datos para el historial. |
| API Spring Boot | 8080            | Aplicación principal.            |

> Nota: La aplicación Spring Boot está configurada para esperar a que la base de datos PostgreSQL esté completamente inicializada antes de iniciar su propio proceso.

## 🌐 Endpoints de la API

### 1. Cálculo con Porcentaje Dinámico

* **URL:** `GET /api/calculator/sum`
* **Descripción:** Realiza la suma de `number1` y `number2`, luego aplica el porcentaje dinámico. Si el servicio externo falla, utiliza el último valor almacenado en caché.

#### Parámetros Query

| Parámetro | Tipo   | Descripción            |
| --------- | ------ | ---------------------- |
| number1   | double | Primer número a sumar  |
| number2   | double | Segundo número a sumar |

#### Respuestas Posibles

| Código | Descripción           | Detalle                                       |
| ------ | --------------------- | --------------------------------------------- |
| 200 OK | Éxito                 | Resultado del cálculo.                        |
| 400    | Bad Request           | Parámetro faltante o formato inválido.        |
| 503    | Service Unavailable   | Servicio externo falló y la caché está vacía. |
| 500    | Internal Server Error | Error inesperado en el procesamiento.         |

#### Ejemplo de Llamada (cURL)

```bash
curl -X GET "http://localhost:8080/api/calculator/sum?number1=10.5&number2=20.0"
```

---

### 2. Historial de Llamadas

* **URL:** `GET /api/history`
* **Descripción:** Recupera el historial de todas las llamadas realizadas a la API, con soporte para paginación y ordenamiento.

#### Parámetros de Paginación (Opcionales)

| Parámetro | Tipo   | Descripción                       | Default        |
| --------- | ------ | --------------------------------- | -------------- |
| page      | int    | Número de página                  | 0              |
| size      | int    | Cantidad de elementos por página  | 20             |
| sort      | string | Campo y dirección de ordenamiento | timestamp,desc |

#### Respuestas Posibles

| Código | Descripción           | Detalle                                       |
| ------ | --------------------- | --------------------------------------------- |
| 200 OK | Éxito                 | Página de resultados del historial.           |
| 400    | Bad Request           | Parámetro de paginación inválido.             |
| 500    | Internal Server Error | Fallo al intentar acceder a la base de datos. |

#### Ejemplo de Llamada (cURL)

```bash
curl -X GET "http://localhost:8080/api/history?page=0&size=10&sort=timestamp,asc"
```

---

## 📄 Documentación Interactiva (Swagger UI)

Una vez que la aplicación esté en ejecución (`http://localhost:8080`), puedes acceder a la interfaz interactiva de Swagger:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

En esta interfaz podrás:

* Ver la descripción y el esquema de cada endpoint.
* Probar las peticiones directamente desde el navegador.
* Visualizar los esquemas de entrada/salida y los códigos de respuesta.

---

## ✉️ Contacto

**Julian Becerra Vega**
Email: [julianbecerravega@gmail.com](mailto:julianbecerravega@gmail.com)

