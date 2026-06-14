# Conclusiones de la Tarea T02.03

El desarrollo del backend del Sistema de Citas Médicas permitió materializar, en una aplicación
funcional, los artefactos producidos en las fases previas: los requerimientos del SRS (T02.01)
y el diseño del DDS (T02.02). Adoptar el esquema modelo/repositorio/servicio/controlador de la
Figura 1 demostró ser una decisión acertada, pues la separación de responsabilidades facilitó
distribuir el trabajo entre los integrantes del grupo y mantener el código ordenado y
comprensible. Cada requerimiento del SRS pudo trazarse directamente a un endpoint y a una clase
de servicio, lo que evidencia la importancia de una especificación y un diseño bien elaborados
antes de programar.

La implementación de la seguridad mediante JWT y control de acceso por roles (RBAC), junto con
la máquina de estados de las citas y el bloqueo de solapamientos, mostró que las reglas de
negocio deben residir en la capa de servicio y no en los controladores. La documentación
automática con Swagger y el despliegue mediante contenedores Docker simplificaron las pruebas y
la entrega. Como aprendizaje principal, comprendimos que trabajar en equipo con un repositorio
de código, tareas asignadas y commits frecuentes mejora la coordinación y refleja el aporte real
de cada miembro, tal como ocurre en un entorno profesional de desarrollo de software.
