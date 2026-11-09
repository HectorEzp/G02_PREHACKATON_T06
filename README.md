# G01_PREHACKATON_T06 — Sistema de Gestión de Eventos y Conferencias

**Curso:** Experiencias Formativas 2 · **Docente:** Valery Chumpitaz · **Práctica:** T06 Pre-Hackatón
**Stack:** Java 17+ · JavaFX 21 · MySQL 8 (JDBC) · JasperReports 6.20 · Maven · arquitectura MVC + DAO

> Renombra el repositorio con tu número de grupo: `G##_PREHACKATON_T06` (ejemplo `G01_PREHACKATON_T06`).

## Integrantes y módulos

| Integrante | Módulo CRUD | Reporte JasperReports |
|---|---|---|
| Integrante 1 — _(nombre)_ | Gestión de Eventos (`EventoController`, `model/dao/EventoDAOImpl`, `view/eventos.fxml`) | `reporte_eventos.jrxml` — General de Eventos y Estados |
| Integrante 2 — _(nombre)_ | Gestión de Participantes (`ParticipanteController`, `ParticipanteDAOImpl`, `view/participantes.fxml`) | `reporte_participantes_institucion.jrxml` — Participantes por Institución |
| Integrante 3 — _(nombre)_ _(solo equipos de 3)_ | Gestión de Inscripciones (`InscripcionController`, `InscripcionDAOImpl`, `view/inscripciones.fxml`) | `reporte_inscritos_evento.jrxml` — Inscritos por Evento |

> Equipos de 2: eliminen el módulo de Inscripciones (controlador, DAO, modelo, FXML, reporte y su botón en `main.fxml`).

## Requisitos

- JDK 17 o superior
- Maven 3.8+
- MySQL 8.0.16+ (por Docker o AWS RDS)

## 1. Base de datos

El script completo está en [`sql/db_eventos.sql`](sql/db_eventos.sql) (crea `db_eventos`, las tablas `eventos`, `participantes`, `inscripciones`, sus PK/FK/UNIQUE/CHECK y datos de prueba).

### Opción A — Docker (puerto 3306)

```bash
docker compose up -d
```

El script se ejecuta automáticamente al crear el contenedor. Usuario `root`, contraseña `root`.
Para reiniciar los datos: `docker compose down -v && docker compose up -d`.

### Opción B — AWS RDS

1. Crea una instancia MySQL 8 en RDS (con acceso público si los integrantes se conectan desde casa).
2. En el **Security Group** agrega una regla de entrada TCP 3306 para las IP de cada integrante.
3. Ejecuta `sql/db_eventos.sql` con MySQL Workbench o CLI contra el endpoint de RDS.
4. Cambia el endpoint, usuario y contraseña en `src/main/resources/db.properties`.

## 2. Configurar y ejecutar

Edita `src/main/resources/db.properties` si tu servidor no es `localhost:3306` con `root/root`.

```bash
mvn clean javafx:run
```

La barra lateral indica el estado de la conexión (`● MySQL conectado`).

## 3. Arquitectura (MVC + DAO)

```
src/main/java/com/prehackaton/eventos
├── Main.java                 # punto de entrada
├── controller/               # Main, Evento, Participante, Inscripcion + Validador
├── model/                    # Evento, Participante, Inscripcion (POJOs)
│   └── dao/                  # Conexion, CrudDAO<T>, interfaces y *DAOImpl, ReporteService -> ÚNICA capa con SQL
└── view/                     # VentanaPrincipal (arranque JavaFX), Alertas, Formularios, ReporteDialog
src/main/resources
├── view/                     # vistas FXML con el estilo en línea (sin CSS, sin SQL)
├── reports/                  # .jrxml (la consulta de cada reporte vive en el .jrxml)
└── db.properties
```

Los controladores y las vistas FXML **no contienen consultas SQL**; solo llaman a los DAO.

## 4. Funcionalidades

**CRUD en cada módulo**
- **Create:** formulario con validaciones antes de insertar.
- **Read:** `TableView` con búsqueda y filtro dinámicos (se actualiza al escribir; Eventos además filtra por estado).
- **Update:** al seleccionar una fila se carga el formulario; se verifica que el registro siga existiendo antes de editar.
- **Delete:** eliminación física previa confirmación; si hay registros relacionados se informa con un mensaje claro.

**Validaciones**

| Regla | Dónde |
|---|---|
| Campos obligatorios | todos los formularios |
| DNI de 8 dígitos (el campo solo admite números), correo con formato válido, teléfono de 9 dígitos, fecha `dd/MM/yyyy`, hora `HH:mm` | `Validador`, controladores |
| Sin duplicados: DNI, correo, evento (nombre + fecha), inscripción (evento + participante) | DAO + controladores + `UNIQUE` en BD |
| Confirmación antes de borrar y alertas contextuales | `Alertas` |
| Regla de negocio: no se admiten inscripciones nuevas en eventos Finalizados o Cancelados | `InscripcionController` |

**Componentes UI usados:** `BorderPane`, `VBox`, `HBox`, `GridPane`, `TextField`, `TextArea`, `ComboBox`, `DatePicker` personalizado (formato local, edición manual, fines de semana resaltados), `TableView`/`TableColumn`, `Button` con iconos y estilos en línea, `Alert` y diálogos.

## 5. Reportes JasperReports

Cada módulo tiene un botón **▤ Reporte** que pide un filtro (estado, institución o evento), genera el reporte con la conexión MySQL activa y permite **verlo en JasperViewer** o **exportarlo a PDF**.

## 6. Evidencias requeridas (guardar en `docs/capturas/`)

- [ ] Interfaz principal, formularios vacíos y llenos, tablas con registros y alertas
- [ ] CRUD operativo: registros insertados, editados y eliminados
- [ ] Ejecución e impresión de los 3 reportes (JasperViewer / PDF)
- [ ] Tablas y registros en MySQL Workbench o CLI (`SHOW TABLES; SELECT * FROM ...`)

## 7. Flujo de trabajo con Git

- Una rama por integrante (`feature/eventos`, `feature/participantes`, `feature/inscripciones`) y *pull request* hacia `main`.
- Cada integrante debe hacer sus propios commits (se evalúan "commits individuales").

## 8. Guion del video (3–5 min)

1. Presentación del equipo y del proyecto
2. Demostración de la aplicación en ejecución
3. Explicación del CRUD por cada integrante (cámara encendida)
4. Generación y visualización de los reportes
5. Interacción con MySQL (consultas en Workbench/CLI)
