# Conclusiones de la Tarea T02.04

La implementacion de pruebas unitarias sobre el backend del Sistema de Citas Medicas nos permitio
comprobar, de forma automatica y repetible, que las reglas de negocio se comportan como fueron
disenadas en el DDS (T02.02). Trabajamos con JUnit 5 como motor de pruebas, Mockito para simular
los repositorios y dependencias, AssertJ para aserciones legibles y JaCoCo para medir la cobertura.
Concentramos el esfuerzo en la capa de servicio, que es donde reside la logica del sistema, logrando
una cobertura del 64,5% de los metodos y del 86,5% de las lineas, por encima del 60% exigido.

El proceso evidencio varias ventajas concretas: las pruebas detectaron de inmediato casos limite
como agendar citas solapadas, transiciones de estado invalidas o el calculo de impuestos en los
comprobantes, y nos dieron confianza para refactorizar sin temor a romper funcionalidad. Tambien
comprendimos el valor de aislar cada unidad mediante mocks, evitando depender de la base de datos.
Como aprendizaje principal, confirmamos que escribir pruebas obliga a disenar codigo mas desacoplado
y con responsabilidades claras, y que la cobertura no es un fin en si misma, sino una guia para
saber que partes criticas del software aun necesitan verificacion. El trabajo se distribuyo entre
los integrantes por modulo, reflejado en los commits del repositorio.
