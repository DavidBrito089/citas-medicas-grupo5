# Tareas de seguimiento y plan de commits — Grupo 5

Este documento cubre el criterio de la rúbrica *"Repositorio correctamente documentado con
los requerimientos, diseño y tareas definidas por usuario"* y *"Al menos 20 commits que
aseguren que han trabajado todos los compañeros"*.

## Asignación de tareas por integrante

| # | Tarea | Responsable | Requerimientos |
|---|-------|-------------|----------------|
| T1 | Configuración del proyecto, seguridad JWT y RBAC | Santiago Brito | RQ-CONF-01 |
| T2 | Módulo de pacientes y colaboradores | Santiago Castillo | RQ-PAC-01, RQ-COL-01 |
| T3 | Agenda de citas, máquina de estados e historial por cédula | Mario Salazar | RQ-CIT-01, RQ-CIT-02 |
| T4 | Historia clínica, prescripciones y certificados | Anthony Vega | RQ-CON-01, RQ-CON-02, RQ-CER-01 |
| T5 | Facturación, contabilidad y reportes | Alisson Valencia | RQ-FIN-01/02/03, RQ-REP-01, RQ-CONF-02/03 |

> Sugerencia: en GitHub, crear estas 5 tareas como *Issues* y un *Project* (tablero Kanban)
> con columnas To Do / In Progress / Done. Cada commit debe referenciar su issue (ej. `#3`).

## Plan de commits sugerido (≥ 20)

Para repartir el trabajo entre los 5 integrantes, cada uno debe ser autor de al menos 4 commits.
Use `git commit --author="Nombre <correo>"` o que cada integrante haga push desde su cuenta.

1. chore: estructura inicial del proyecto Spring Boot y pom.xml
2. feat: entidad base, configuración H2/Postgres y OpenAPI
3. feat: seguridad JWT, filtro y SecurityConfig (RQ-CONF-01)
4. feat: registro/login/refresh de usuarios (RQ-CONF-01)
5. feat: gestión de usuarios y roles
6. feat: entidad y CRUD de pacientes (RQ-PAC-01)
7. feat: búsqueda de paciente por cédula
8. feat: entidad y CRUD de colaboradores/médicos (RQ-COL-01)
9. feat: entidad Appointment y repositorio
10. feat: agendamiento de citas con duración por tipo (RQ-CIT-01)
11. feat: bloqueo de solapamientos de citas
12. feat: máquina de estados de la cita
13. feat: servicio web de historial por cédula (RQ-CIT-02)
14. feat: historia clínica / consultas (RQ-CON-01)
15. feat: prescripciones y órdenes de examen (RQ-CON-02)
16. feat: certificados médicos (RQ-CER-01)
17. feat: plan de cuentas contable (RQ-CONF-02)
18. feat: comprobantes de venta y facturación a terceros (RQ-FIN-01, RQ-PAC-02)
19. feat: libro diario automático y cuentas contables (RQ-FIN-02, RQ-FIN-03)
20. feat: reportes de libro diario, historia y comprobantes (RQ-REP-01)
21. feat: parámetros de la institución (RQ-CONF-03)
22. feat: auditoría de operaciones (audit_logs)
23. test: pruebas unitarias de la agenda y prueba de contexto
24. docs: README, trazabilidad y Dockerfile/compose para despliegue

## Despliegue

- Local/contenedor: `docker compose up --build`
- Documentación viva en `/swagger-ui.html`
- Para una demo pública se puede desplegar el contenedor en Render, Railway o una VM con Docker.
