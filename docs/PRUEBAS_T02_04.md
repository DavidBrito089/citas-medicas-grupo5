# Pruebas unitarias y cobertura — Tarea T02.04

## Frameworks utilizados (Java)

- **JUnit 5** — motor de ejecucion de las pruebas.
- **Mockito** — simulacion (mocks) de repositorios y dependencias para aislar cada unidad.
- **AssertJ** — aserciones fluidas y legibles (`assertThat(...)`).
- **JaCoCo** — analisis de cobertura de codigo.

> Nota: PowerMock no se utilizo porque no es compatible de forma estable con JUnit 5 y no fue
> necesario: el diseno basado en inyeccion de dependencias permite simular todo con Mockito.

## Como ejecutar las pruebas y la cobertura

```bash
# Ejecuta las pruebas
./mvnw test

# Ejecuta pruebas + genera el reporte de cobertura + valida el umbral del 60%
./mvnw clean verify
```

El reporte HTML de cobertura queda en: `target/site/jacoco/index.html`

La fase `verify` **falla automaticamente** si la cobertura de metodos cae por debajo del **60%**
(regla configurada en el `pom.xml` con el `jacoco-maven-plugin`).

## Resultados obtenidos

- **83 pruebas** unitarias, todas en verde.
- **Cobertura de metodos: 64,5%** (supera el 60% exigido).
- **Cobertura de lineas: 86,5%**.

| Paquete | Metodos cubiertos |
|---|---|
| service | 83% |
| exception | 88% |
| security | 69% |
| enums | 100% |

Se midio la cobertura sobre el codigo con logica de negocio; se excluyeron del analisis las clases
sin comportamiento (entidades `model`, `dto`, `config`, arranque y datos semilla `bootstrap`).

## Que se prueba

- **Servicios** (capa de negocio): autenticacion/JWT, usuarios, pacientes, colaboradores, citas
  (duracion por tipo, **bloqueo de solapamientos**, **maquina de estados**, historial por cedula),
  historia clinica, prescripciones, certificados, facturacion (calculo de subtotal/IVA/total),
  contabilidad y libro diario, configuracion y reportes.
- **Seguridad**: generacion y validacion de tokens JWT.
- **Manejo de errores**: el `GlobalExceptionHandler` y el formato de error estandar.
