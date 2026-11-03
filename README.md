# Empleados API

API REST para gestionar empleados, desarrollada con Java 21 y Quarkus. Usa Hibernate ORM con Panache y permite trabajar con H2 persistente o PostgreSQL en Docker.

## Funcionalidades

- Crear, listar, consultar, actualizar y eliminar empleados.
- Actualizar parcialmente el departamento, el puesto o el salario mediante `PATCH`.
- Validar los datos de cada empleado.
- Proteger los endpoints de empleados con autenticación JWT y roles.
- Consultar la especificación OpenAPI.
- Persistir registros en H2 en disco o en PostgreSQL.

## Tecnologías

- Java 21
- Quarkus 3.40.1
- Maven Wrapper
- Hibernate ORM con Panache
- H2
- PostgreSQL
- Docker y Docker Compose
- SmallRye JWT
- REST y JSON
- OpenAPI y Swagger UI

## Modelo y validaciones

Cada empleado tiene estos campos:

- `nombre`: obligatorio y no puede estar vacío.
- `departamento`: obligatorio y no puede estar vacío.
- `puesto`: obligatorio y no puede estar vacío.
- `salario`: obligatorio, mayor que 0 y menor que 80000.

El ID se genera al crear el empleado.

## Endpoints

| Método | Endpoint | Acceso | Respuestas |
|---|---|---|---|
| `POST` | `/empleados` | `ADMIN` | `201`, `400`, `401`, `403` |
| `GET` | `/empleados` | `ADMIN` | `200`, `401`, `403` |
| `GET` | `/empleados/{id}` | `ADMIN`, `EMPLEADO` | `200`, `401`, `403`, `404` |
| `PUT` | `/empleados/{id}` | `ADMIN` | `200`, `400`, `401`, `403`, `404` |
| `PATCH` | `/empleados/{id}` | `ADMIN` | `200`, `400`, `401`, `403`, `404` |
| `DELETE` | `/empleados/{id}` | `ADMIN` | `204`, `401`, `403`, `404` |

`PUT` requiere los cuatro campos del empleado. `PATCH` acepta uno o más de estos campos: `departamento`, `puesto` y `salario`. Los campos omitidos conservan sus valores y los datos inválidos se rechazan.

### Crear un empleado

Solicitud `POST /empleados`:

```json
{
  "nombre": "Carolina Neri",
  "departamento": "TI",
  "puesto": "Desarrolladora",
  "salario": 35000.00
}
```

La respuesta es `201 Created`. El encabezado `Location` indica la URL del empleado creado. La respuesta no incluye un cuerpo.

### Actualizar parcialmente un empleado

Solicitud `PATCH /empleados/1`:

```json
{
  "puesto": "Analista"
}
```

La respuesta `200 OK` contiene el empleado actualizado. Los demás campos conservan sus valores.

## Seguridad JWT

Todos los endpoints bajo `/empleados` requieren un token JWT en el encabezado `Authorization`:

```text
Authorization: Bearer <token>
```

- El rol `ADMIN` puede listar y realizar todas las operaciones CRUD.
- El rol `EMPLEADO` puede consultar únicamente `GET /empleados/{id}` cuando el ID coincide con el `sub` del token.
- Los roles se leen de la propiedad `groups` del JWT.
- El emisor esperado (`issuer`) es `empleados-api`.
- La API valida tokens; no incluye un endpoint de inicio de sesión ni emite tokens.

Respuestas de seguridad:

- `401 Unauthorized`: falta el token o no es válido.
- `403 Forbidden`: el token es válido, pero no tiene permiso para la operación.

En desarrollo y pruebas, Quarkus puede generar claves temporales en memoria. Esta configuración no está habilitada para producción. Para producción se debe configurar la clave pública de confianza y el emisor. Las claves privadas y las credenciales deben mantenerse fuera del repositorio.

## Códigos HTTP

- `200 OK`: consulta o actualización correcta.
- `201 Created`: empleado creado.
- `204 No Content`: empleado eliminado correctamente.
- `400 Bad Request`: datos inválidos.
- `401 Unauthorized`: falta el token o no es válido.
- `403 Forbidden`: el usuario autenticado no tiene permiso para la operación.
- `404 Not Found`: no existe un empleado con ese ID.

## Requisitos

- JDK 21 para ejecutar Quarkus localmente y las pruebas.
- Git.
- Docker Desktop para usar PostgreSQL con Docker Compose.
- El proyecto incluye Maven Wrapper.

## Ejecución local con H2

Desde la carpeta raíz del proyecto, ejecuta:

```powershell
.\mvnw.cmd quarkus:dev
```

La aplicación usa H2, la base predeterminada. En este modo están disponibles:

- API: `http://localhost:8080/empleados`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Especificación OpenAPI: `http://localhost:8080/q/openapi`
- Quarkus Dev UI: `http://localhost:8080/q/dev/`

Para detener el modo de desarrollo, presiona `Ctrl+C`.

## Ejecutar la API y PostgreSQL con Docker Compose

Crea un archivo `.env` en la raíz del proyecto con las credenciales locales de PostgreSQL:

```text
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
```

No subas `.env` ni sus credenciales a Git. Docker Compose lee estas variables y las entrega a los contenedores. La API se ejecuta con el perfil `prod` y se conecta a PostgreSQL por medio del nombre del servicio `postgres`.

Desde la raíz del proyecto, valida la configuración e inicia los servicios:

```powershell
docker compose config --quiet
docker compose up --build -d
docker compose ps
```

El comando `docker compose ps` debe mostrar la API en ejecución y PostgreSQL como `healthy`.

La API queda disponible en:

- API: `http://localhost:8080/empleados`
- Especificación OpenAPI: `http://localhost:8080/q/openapi`

La última comprobación de OpenAPI devolvió el código HTTP `200`. Swagger UI está disponible durante el desarrollo local; no se incluye en el perfil `prod` con la configuración actual.

Para ver los logs de la API:

```powershell
docker compose logs -f api
```

Presiona `Ctrl+C` para dejar de seguir los logs.

Para detener los servicios:

```powershell
docker compose down
```

Este comando conserva el volumen `empleados_postgres_data` y los registros de PostgreSQL. No uses `docker compose down -v` a menos que quieras borrar ese volumen y sus datos.

### Validar un reinicio limpio

Para recrear los contenedores y volver a probar el arranque:

```powershell
docker compose down
docker compose config --quiet
docker compose up --build -d
docker compose ps
```

Comprueba que OpenAPI responda `200`:

```powershell
(Invoke-WebRequest -Uri 'http://localhost:8080/q/openapi' -UseBasicParsing).StatusCode
```

## Ejecutar Quarkus en modo desarrollo con PostgreSQL

Para ejecutar la API desde Maven y usar PostgreSQL, primero detén el stack completo si está activo y levanta únicamente la base:

```powershell
docker compose down
docker compose up -d postgres
```

Define en PowerShell las mismas credenciales que configuraste en `.env` y activa el perfil `postgres`:

```powershell
$env:DB_USERNAME = 'tu_usuario'
$env:DB_PASSWORD = 'tu_contraseña'
$env:QUARKUS_PROFILE = 'postgres'
.\mvnw.cmd quarkus:dev
```

El perfil `postgres` se conecta a `localhost:5432`, donde Docker Compose publica PostgreSQL. Al terminar, presiona `Ctrl+C` para detener Quarkus.

## Pruebas

Para ejecutar las pruebas automatizadas en Windows:

```powershell
.\mvnw.cmd test
```

Última ejecución local registrada: **31/10/2026 — 18 pruebas, 0 fallos y 0 errores**. Las pruebas utilizan H2 en memoria e incluyen casos de autorización y validación de JWT firmados.

GitHub Actions ejecuta las pruebas con Java 21 en cada `push` y `pull_request`, mediante el flujo `.github/workflows/ci.yml`. La ejecución **CI #1** pasó correctamente para el commit `5b9b72a`.

## Persistencia

Hibernate ORM sincroniza el esquema con las entidades mediante la estrategia `update`.

### H2

La configuración predeterminada usa `jdbc:h2:file:./data/empleados`. Los datos se guardan en disco dentro de `data/`.

### PostgreSQL

Docker Compose guarda los datos en el volumen `empleados_postgres_data`. El volumen conserva los registros cuando se detienen y recrean los contenedores con `docker compose down` y `docker compose up`.

Las credenciales locales se configuran en `.env`, que está excluido de Git.

## Empaquetar la aplicación

Para generar el paquete de Quarkus:

```powershell
.\mvnw.cmd package
```

El resultado se genera en `target/quarkus-app/`. El `Dockerfile` construye la imagen de la API y Docker Compose la inicia junto con PostgreSQL.

## Estructura principal

```text
empleados-api/
├── .github/
│   └── workflows/
│       └── ci.yml
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── DetalleValidacion.java
│   │   │   ├── Empleado.java
│   │   │   ├── EmpleadoMapper.java
│   │   │   ├── EmpleadoPatchRequest.java
│   │   │   ├── EmpleadoRequest.java
│   │   │   ├── EmpleadoResponse.java
│   │   │   ├── EmpleadoService.java
│   │   │   ├── EmpleadosResource.java
│   │   │   ├── RespuestaError.java
│   │   │   └── ValidationExceptionMapper.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/example/
│       │   ├── EmpleadosJwtIntegracionTest.java
│       │   ├── EmpleadosResourceTest.java
│       │   └── EmpleadosSeguridadTest.java
│       └── resources/
├── .dockerignore
├── .gitignore
├── compose.yaml
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

`.env`, `data/` y `target/` son locales o generados y no forman parte de la estructura pública del repositorio. `.env` está excluido de Git.

## Estado del proyecto

La API implementa endpoints CRUD y `PATCH`, protegidos mediante JWT y roles. Las pruebas locales pasaron con **18 pruebas, 0 fallos y 0 errores**. El flujo CI de GitHub Actions también terminó correctamente para el commit `5b9b72a`. La API y PostgreSQL se levantaron con Docker Compose y se confirmó que `/q/openapi` respondía `200`.

## Documentación oficial

- [Quarkus](https://quarkus.io/)
- [Quarkus Security JWT](https://quarkus.io/guides/security-jwt)
- [Hibernate ORM con Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Configuración de bases de datos](https://quarkus.io/guides/datasource)
- [OpenAPI y Swagger UI](https://quarkus.io/version/3.40/guides/openapi-swaggerui/)
- [Docker Compose](https://docs.docker.com/compose/)
- [GitHub Actions](https://docs.github.com/actions)

## Autora

Ing. Carolina Neri Garduño