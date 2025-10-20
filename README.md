# Challenge Backend - API REST en Spring Boot

## Descripción

Este proyecto implementa una API REST en Spring Boot (Java 21) con las siguientes funcionalidades:

- **Cálculo con porcentaje dinámico:** Suma dos números y aplica un porcentaje adicional obtenido de un servicio externo (mock).
- **Caché del porcentaje:** El porcentaje se almacena en memoria durante 30 minutos para usarlo si el servicio externo falla.
- **Historial de llamadas:** Guarda un historial de todas las llamadas realizadas a la API (fecha, endpoint, parámetros, respuesta/error) de forma asíncrona en una base de datos PostgreSQL.

La aplicación se ejecuta en contenedores Docker junto con la base de datos PostgreSQL usando docker-compose.

## Requisitos previos

- Tener instalado Docker y Docker Compose.
- (Opcional) Navegador para probar la documentación Swagger.

## Levantar la aplicación con Docker Compose

Clona este repositorio:

```bash
git clone <url-del-repositorio>
cd <nombre-del-repositorio>
```

Configura las variables de entorno creando un archivo .env con las credenciales para PostgreSQL:
```
POSTGRES_USER=usuario
POSTGRES_PASSWORD=contraseña
POSTGRES_DB=challenge_db
```

Esto hará lo siguiente:

- Levantar un contenedor de PostgreSQL en el puerto 5432.
- Construir y levantar la aplicación Spring Boot en el puerto 8080.
- La aplicación esperará a que la base de datos esté lista antes de iniciar.
- Verifica que ambos servicios estén corriendo sin errores.



## Endpoints disponibles
### 1. Cálculo con porcentaje dinámico

URL: GET /api/calculator/sum

Descripción: Suma dos números (number1 y number2) y aplica un porcentaje adicional obtenido de un servicio externo (mock). Si el servicio externo falla, usa el último porcentaje almacenado en caché.

Parámetros Query:

number1 (double) - Primer número a sumar.

number2 (double) - Segundo número a sumar.

Respuestas:

200 OK con el resultado calculado.

400 Bad Request si falta un parámetro o es inválido.

503 Service Unavailable si el servicio externo falla y no hay caché disponible.

500 Internal Server Error en caso de error inesperado.

Ejemplo:
```
curl "http://localhost:8080/api/calculator/sum?number1=10&number2=20"
```


### 2. Historial de llamadas

URL: GET /api/history

Descripción: Obtiene un historial paginado de las llamadas realizadas a la API, incluyendo fecha, endpoint, parámetros, respuesta o error.

Parámetros de paginación: (opcional, mediante query params estándar de Spring Data)

page (número de página, default 0)

size (tamaño de página, default 20)

sort (ordenamiento, por defecto fecha descendente)

Respuestas:

200 OK con la página de resultados.

400 Bad Request si algún parámetro de paginación es inválido.

500 Internal Server Error en caso de error inesperado.

Ejemplo:
```
curl "http://localhost:8080/api/history?page=0&size=10"
```

## Documentación y pruebas con Swagger

Una vez que la aplicación esté corriendo, puedes acceder a la documentación interactiva y probar los endpoints en:

http://localhost:8080/swagger-ui/index.html


En esta interfaz podrás:

Ver la descripción de cada endpoint.

Probar las peticiones directamente desde el navegador.

Ver los esquemas de entrada y salida.

Visualizar los códigos de respuesta esperados.

## Contacto
Julian Becerra Vega - julianbecerravega@gmail.com
