# Empleados API

API REST para gestionar empleados, desarrollada con Java 21 y Quarkus. Usa Hibernate ORM with Panache y una base de datos H2 persistente.

## Funcionalidades

- Crear, listar, consultar, actualizar y eliminar empleados.
- Actualizar parcialmente el departamento, el puesto o el salario mediante `PATCH`.
- Validar los datos del empleado.
- Consultar y probar los endpoints mediante Swagger UI.
- Persistir los registros en H2 en disco.

## Tecnologías

- Java 21
- Quarkus 3.40.1
- Maven Wrapper
- Hibernate ORM with Panache
- H2 Database
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
- El proyecto incluye Maven Wrapper, por lo que se puede ejecutar Maven mediante los scripts incluidos.

## Ejecución local en Windows

Desde la carpeta raíz del proyecto, ejecuta:

```powershell
.\mvnw.cmd quarkus:dev
```

La aplicación estará disponible en:

- API: `http://localhost:8080/empleados`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Quarkus Dev UI: `http://localhost:8080/q/dev/`

## Pruebas

Para ejecutar las pruebas automatizadas:

```powershell
.\mvnw.cmd test
```

Última ejecución registrada: **11 pruebas, 0 fallos y 0 errores**. Las pruebas usan el perfil `test` y una base H2 en memoria.

## Persistencia

En desarrollo, H2 almacena los datos en disco mediante la URL `jdbc:h2:file:./data/empleados`. La configuración está en:

`src/main/resources/application.properties`

Hibernate usa la estrategia `update` para sincronizar el esquema existente con las entidades.

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

## Estado del proyecto

La API implementa los endpoints CRUD y `PATCH`. La suite automatizada pasó con 11 pruebas. `PATCH` también se verificó manualmente con la base H2 persistente: una actualización válida modificó solo el campo enviado y una solicitud con salario de tipo incorrecto respondió `400` sin aplicar cambios parciales.

## Documentación oficial

- [Quarkus](https://quarkus.io/)
- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Configuración de bases de datos](https://quarkus.io/guides/datasource)
- [OpenAPI y Swagger UI](https://quarkus.io/guides/openapi-swaggerui)

## Autora

Ing. Carolina Neri Garduño