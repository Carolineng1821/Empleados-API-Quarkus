# Empleados API

API REST para gestionar empleados, desarrollada con Java 21 y Quarkus. Usa Hibernate ORM with Panache y permite trabajar con H2 persistente o PostgreSQL en Docker.

## Funcionalidades

- Crear, listar, consultar, actualizar y eliminar empleados.
- Actualizar parcialmente el departamento, el puesto o el salario mediante `PATCH`.
- Validar los datos del empleado.
- Consultar y probar los endpoints mediante Swagger UI.
- Persistir registros en H2 en disco o en PostgreSQL.

## Tecnologías

- Java 21
- Quarkus 3.40.1
- Maven Wrapper
- Hibernate ORM with Panache
- H2 Database
- PostgreSQL
- Docker Compose
- REST y JSON
- OpenAPI / Swagger UI

## Modelo y validaciones

Cada empleado tiene estos campos:

- `nombre`: obligatorio y no puede estar vacío.
- `departamento`: obligatorio y no puede estar vacío.
- `puesto`: obligatorio y no puede estar vacío.
- `salario`: obligatorio, mayor que 0 y menor que 80000.

El ID se genera al crear el empleado.

## Endpoints

| Método | Endpoint | Descripción | Respuestas |
|---|---|---|---|
| `POST` | `/empleados` | Crear un empleado | `201`, `400` |
| `GET` | `/empleados` | Listar empleados | `200` |
| `GET` | `/empleados/{id}` | Consultar por ID | `200`, `404` |
| `PUT` | `/empleados/{id}` | Reemplazar los campos del empleado | `200`, `400`, `404` |
| `PATCH` | `/empleados/{id}` | Actualizar departamento, puesto o salario | `200`, `400`, `404` |
| `DELETE` | `/empleados/{id}` | Eliminar por ID | `204`, `404` |

`PUT` requiere los cuatro campos del empleado. `PATCH` acepta uno o más de estos campos: `departamento`, `puesto` y `salario`; conserva los campos omitidos y rechaza datos inválidos.

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

La respuesta es `201 Created`. El encabezado `Location` indica la URL del empleado creado. El endpoint no devuelve un cuerpo en la respuesta.

### Actualizar parcialmente un empleado

Solicitud `PATCH /empleados/1`:

```json
{
  "puesto": "Analista"
}
```

La respuesta `200 OK` contiene el empleado actualizado. Los demás campos conservan sus valores.

### Códigos HTTP

- `200 OK`: consulta o actualización correcta.
- `201 Created`: empleado creado.
- `204 No Content`: empleado eliminado correctamente.
- `400 Bad Request`: datos inválidos.
- `404 Not Found`: no existe un empleado con ese ID.

## Requisitos

- JDK 21.
- Git.
- Docker Desktop para ejecutar PostgreSQL localmente.
- El proyecto incluye Maven Wrapper.

## Ejecución local en Windows

Desde la carpeta raíz del proyecto, ejecuta:

```powershell
.\mvnw.cmd quarkus:dev
```

Este comando utiliza H2, la base predeterminada.

La aplicación estará disponible en:

- API: `http://localhost:8080/empleados`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Quarkus Dev UI: `http://localhost:8080/q/dev/`

### Ejecutar con PostgreSQL

El archivo `compose.yaml` inicia PostgreSQL en Docker. Crea un archivo `.env` en la raíz del proyecto con estas variables:

```text
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
```

No subas el archivo `.env` ni sus credenciales a Git. Docker Compose lo lee para configurar el contenedor. Quarkus, iniciado desde PowerShell, necesita que las variables también estén definidas en esa sesión. [Docker: variables de entorno en Compose](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation) · [Quarkus: configuración y variables de entorno](https://quarkus.io/guides/config-reference/)

Inicia PostgreSQL:

```powershell
docker compose up -d
```

Define las mismas credenciales en PowerShell y activa el perfil `postgres`. Sustituye los textos de ejemplo por los valores locales de `.env`:

```powershell
$env:DB_USERNAME = 'tu_usuario'
$env:DB_PASSWORD = 'tu_contraseña'
$env:QUARKUS_PROFILE = 'postgres'
.\mvnw.cmd quarkus:dev
```

La conexión PostgreSQL usa `localhost:5432` y la base `empleados`. Docker Compose guarda sus datos en el volumen `empleados_postgres_data`.

## Pruebas

Para ejecutar las pruebas automatizadas:

```powershell
.\mvnw.cmd test
```

Última ejecución registrada: **11 pruebas, 0 fallos y 0 errores**. Las pruebas usan el perfil `test` y una base H2 en memoria.

## Persistencia

La aplicación puede utilizar H2 en disco o PostgreSQL en Docker. Hibernate sincroniza el esquema con las entidades mediante la estrategia `update`.

### H2

La configuración predeterminada usa la URL `jdbc:h2:file:./data/empleados`. Los datos se guardan en disco dentro de la carpeta `data`.

### PostgreSQL con Docker

Las credenciales locales se configuran en `.env`, junto a `compose.yaml`. El volumen `empleados_postgres_data` conserva los datos de PostgreSQL cuando se reinicia Quarkus o el contenedor.

La conexión con PostgreSQL y las operaciones CRUD se verificaron manualmente con Postman. También se confirmó que un empleado de prueba siguió disponible después de reiniciar Quarkus con el perfil `postgres`.

## Empaquetar la aplicación

Para generar el paquete:

```powershell
.\mvnw.cmd package
```

El resultado se genera en `target/quarkus-app/`. Para ejecutarlo:

```powershell
java -jar target/quarkus-app/quarkus-run.jar
```

## Estructura principal

```text
empleados-api/
├── compose.yaml
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── Empleado.java
│   │   │   ├── EmpleadoPatchRequest.java
│   │   │   ├── EmpleadosResource.java
│   │   │   └── RespuestaErrorRecurso.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/EmpleadosResourceTest.java
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

El archivo `.env` es local y está excluido de Git; no se incluye en la estructura pública del proyecto.

## Estado del proyecto

La API implementa los endpoints CRUD y `PATCH`. La suite automatizada pasó con 11 pruebas. Las operaciones CRUD se verificaron manualmente con PostgreSQL en Docker, incluyendo la persistencia de un empleado después de reiniciar Quarkus.

## Documentación oficial

- [Quarkus](https://quarkus.io/)
- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Configuración de bases de datos](https://quarkus.io/guides/datasource)
- [OpenAPI y Swagger UI](https://quarkus.io/guides/openapi-swaggerui)
- [Docker Compose](https://docs.docker.com/compose/)

## Autora

Ing. Carolina Neri Garduño