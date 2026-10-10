# Empleados API

API REST para la gestión de empleados, desarrollada con Java 21 y Quarkus. Implementa operaciones CRUD para crear, consultar, actualizar y eliminar empleados, utilizando Hibernate ORM with Panache y una base de datos H2.

## Objetivo

Desarrollar una API REST como proyecto de portafolio para practicar desarrollo backend con Java, persistencia de datos y servicios HTTP.

## Funcionalidades

- Crear empleados.
- Listar todos los empleados.
- Buscar empleados por ID.
- Actualizar el nombre de un empleado.
- Eliminar empleados por ID.
- Persistir los registros en una base de datos H2 almacenada en disco.
- Consultar y probar los endpoints mediante Swagger UI.

## Tecnologías

- Java 21
- Quarkus 3.40.1
- Maven
- Hibernate ORM with Panache
- H2 Database
- REST y JSON
- OpenAPI / Swagger UI
- IntelliJ IDEA

## Endpoints

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/empleados` | Crear un empleado |
| GET | `/empleados` | Listar todos los empleados |
| GET | `/empleados/{id}` | Consultar un empleado por ID |
| PUT | `/empleados/{id}` | Actualizar un empleado |
| DELETE | `/empleados/{id}` | Eliminar un empleado |

### Ejemplo: crear un empleado

Solicitud `POST /empleados`:

```json
{
  "nombre": "Carolina"
}
```

Respuesta de ejemplo:

```json
{
  "id": 1,
  "nombre": "Carolina"
}
```

El ID es ilustrativo; lo genera la base de datos.

### Códigos HTTP utilizados

- `200 OK`: consulta o actualización correcta.
- `204 No Content`: operación completada sin contenido de respuesta.
- `404 Not Found`: el empleado solicitado no existe.

## Requisitos

- JDK 21.
- Git.
- Maven Wrapper incluido en el proyecto.

## Ejecución local

En Windows, abre una terminal en la raíz del proyecto y ejecuta:

```powershell
.\mvnw.cmd quarkus:dev
```

La aplicación estará disponible en:

- API: `http://localhost:8080/empleados`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Quarkus Dev UI: `http://localhost:8080/q/dev/`

## Empaquetar la aplicación

Para generar el paquete de la aplicación:

```powershell
.\mvnw.cmd package
```

El resultado se genera en `target/quarkus-app/`.

Para ejecutarlo después del empaquetado:

```powershell
java -jar target/quarkus-app/quarkus-run.jar
```

## Persistencia

La aplicación utiliza H2 con almacenamiento en disco para conservar los registros después de reiniciar Quarkus.

La configuración de la base de datos se encuentra en:

`src/main/resources/application.properties`

## Estructura principal

```text
empleados-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── Empleado.java
│   │   │   └── EmpleadosResource.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Aprendizajes

- Diseño de endpoints REST.
- Uso de los métodos HTTP para operaciones CRUD.
- Manejo de respuestas HTTP y códigos de error.
- Persistencia de entidades con Hibernate ORM y Panache.
- Pruebas manuales con Swagger UI.
- Configuración de una base de datos H2 persistente.

## Estado del proyecto

**Primera versión funcional.** Las operaciones CRUD se implementaron y probaron manualmente mediante Swagger UI. Las pruebas automatizadas y las validaciones de datos quedan como mejoras futuras.

## Documentación oficial

- [Quarkus](https://quarkus.io/)
- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Configuración de bases de datos](https://quarkus.io/guides/datasource)
- [OpenAPI y Swagger UI](https://quarkus.io/guides/openapi-swaggerui)

## Autora

Ing. Carolina Neri Garduño