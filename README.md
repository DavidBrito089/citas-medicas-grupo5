# Sistema de Citas Médicas — Backend (Tarea T02.03, Grupo 5)

Backend del **Sistema de Citas Médicas** desarrollado para la asignatura de Ingeniería de
Software. Implementa los requerimientos definidos en el **SRS (T02.01)** y el diseño descrito
en el **DDS (T02.02)**, siguiendo el esquema **modelo / repositorio / servicio / controlador**
de la Figura 1 del enunciado.

- **Framework:** Spring Boot 3.2 (Java 17)
- **Persistencia:** Spring Data JPA — H2 en memoria (desarrollo) / PostgreSQL (producción)
- **Seguridad:** Spring Security + JWT (RBAC con roles ADMIN, MEDICO, RECEPCIONISTA, PACIENTE)
- **Documentación:** OpenAPI / Swagger UI

## Arquitectura

```
com.grupo5.citasmedicas
├── model         Entidades JPA (User, Patient, Appointment, MedicalRecord, Invoice, ...)
├── repository    Repositorios Spring Data (acceso a datos)
├── service       Lógica de negocio y reglas (estados de cita, solapamientos, contabilidad)
├── controller    Controladores REST (endpoints documentados con Swagger)
├── dto           Objetos de entrada (request) y salida (response)
├── security      JWT, filtro de autenticación y UserDetails
├── config        Configuración de seguridad
├── exception     Manejo global de errores (formato { code, message, details })
└── bootstrap     Carga de datos iniciales (seed)
```

## Cómo ejecutar

### Opción A — Docker Compose (recomendada, no requiere Maven ni Postgres locales)

```bash
docker compose up --build
```

La API queda en `http://localhost:8080` con PostgreSQL como base de datos.

### Opción B — Local con Maven Wrapper (perfil H2 en memoria)

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

No necesita base de datos: usa H2 en memoria. Consola H2 en `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:citasmedicas`, usuario `sa`, sin contraseña).

## Documentación de la API (Swagger)

Con la aplicación levantada:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Usuarios de prueba (seed inicial)

| Usuario     | Contraseña     | Rol           |
|-------------|----------------|---------------|
| `admin`     | `admin123`     | ADMIN         |
| `medico`    | `medico123`    | MEDICO        |
| `recepcion` | `recepcion123` | RECEPCIONISTA |

## Ejemplo de uso rápido

```bash
# 1. Login -> obtener access_token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Usar el token (RQ-CIT-02: historial de citas por cédula)
curl http://localhost:8080/api/v1/appointments/historial/0107654321 \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

## Trazabilidad de requerimientos (SRS → implementación)

| Requerimiento | Descripción                                   | Endpoint / Módulo                         |
|---------------|-----------------------------------------------|-------------------------------------------|
| RQ-CONF-01    | Gestión de usuarios y roles                   | `/api/v1/auth`, `/api/v1/users`           |
| RQ-CONF-02    | Plan de cuentas contable                      | `/api/v1/accounting/accounts`             |
| RQ-CONF-03    | Parámetros de la institución                  | `/api/v1/config`                          |
| RQ-PAC-01     | Registro de pacientes                         | `/api/v1/patients`                        |
| RQ-PAC-02     | Facturación a terceros                        | `/api/v1/invoices` (campo `facturarA`)    |
| RQ-COL-01     | Gestión de médicos y colaboradores            | `/api/v1/collaborators`                   |
| RQ-CIT-01     | Agenda de citas                               | `/api/v1/appointments`                    |
| RQ-CIT-02     | Servicio web: historial por cédula            | `/api/v1/appointments/historial/{cedula}` |
| RQ-CON-01     | Historia clínica                              | `/api/v1/medical_records`                 |
| RQ-CON-02     | Prescripciones y órdenes de examen            | `/api/v1/prescriptions`                   |
| RQ-CER-01     | Certificados médicos                          | `/api/v1/certificates`                    |
| RQ-FIN-01     | Comprobantes de venta                         | `/api/v1/invoices`                        |
| RQ-FIN-02     | Registro automático en diario de caja         | `/api/v1/accounting/journal`              |
| RQ-FIN-03     | Asignación a cuentas contables                | `AccountingService.recordInvoiceIncome`   |
| RQ-REP-01     | Reportes (libro diario, historia, comprobantes)| `/api/v1/reports`                        |

## Pruebas

```bash
./mvnw test
```

Incluye una prueba de humo del contexto y pruebas unitarias de las reglas de negocio de la
agenda (duración por defecto y bloqueo de solapamientos).

## Equipo — Grupo 5

- Brito Vega Santiago David
- Castillo Ochoa Santiago David
- Salazar Serna Mario Alexander
- Vega Cruz Anthony Jhossua
- Valencia Larrea Alisson Nicole
