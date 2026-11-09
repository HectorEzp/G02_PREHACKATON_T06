-- =====================================================================
-- Script SQL: Sistema de Gestión de Eventos y Conferencias (T06)
-- Motor: MySQL 8.0+  (los CHECK se aplican desde 8.0.16)
-- =====================================================================
DROP DATABASE IF EXISTS db_eventos;
CREATE DATABASE db_eventos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_eventos;

-- ---------------------------------------------------------------------
-- Tabla: eventos
-- ---------------------------------------------------------------------
CREATE TABLE eventos (
    id_evento   INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(120) NOT NULL,
    descripcion VARCHAR(500) NULL,
    fecha       DATETIME     NOT NULL,
    lugar       VARCHAR(120) NOT NULL,
    modalidad   VARCHAR(20)  NOT NULL,
    estado      VARCHAR(20)  NOT NULL DEFAULT 'Programado',
    CONSTRAINT uq_evento_nombre_fecha UNIQUE (nombre, fecha),
    CONSTRAINT ck_evento_modalidad CHECK (modalidad IN ('Presencial', 'Virtual', 'Híbrido')),
    CONSTRAINT ck_evento_estado CHECK (estado IN ('Programado', 'En curso', 'Finalizado', 'Cancelado'))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Tabla: participantes
-- ---------------------------------------------------------------------
CREATE TABLE participantes (
    id_participante INT AUTO_INCREMENT PRIMARY KEY,
    nombres         VARCHAR(80)  NOT NULL,
    apellidos       VARCHAR(80)  NOT NULL,
    dni             CHAR(8)      NOT NULL,
    correo          VARCHAR(100) NOT NULL,
    telefono        VARCHAR(15)  NULL,
    institucion     VARCHAR(120) NOT NULL,
    CONSTRAINT uq_participante_dni UNIQUE (dni),
    CONSTRAINT uq_participante_correo UNIQUE (correo)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Tabla: inscripciones (relación N:M resuelta entre eventos y participantes)
-- ---------------------------------------------------------------------
CREATE TABLE inscripciones (
    id_inscripcion    INT AUTO_INCREMENT PRIMARY KEY,
    id_evento         INT         NOT NULL,
    id_participante   INT         NOT NULL,
    fecha_inscripcion DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado            VARCHAR(20) NOT NULL DEFAULT 'Inscrito',
    CONSTRAINT fk_insc_evento FOREIGN KEY (id_evento)
        REFERENCES eventos (id_evento) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_insc_participante FOREIGN KEY (id_participante)
        REFERENCES participantes (id_participante) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_insc_evento_participante UNIQUE (id_evento, id_participante),
    CONSTRAINT ck_insc_estado CHECK (estado IN ('Inscrito', 'Confirmado', 'Cancelado'))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Datos de prueba
-- ---------------------------------------------------------------------
INSERT INTO eventos (nombre, descripcion, fecha, lugar, modalidad, estado) VALUES
('Congreso de Innovación Tecnológica', 'Charlas sobre IA y desarrollo de software', '2026-11-20 09:00:00', 'Auditorio Principal', 'Presencial', 'Programado'),
('Taller de Ciberseguridad', 'Buenas prácticas de seguridad para desarrolladores', '2026-11-25 15:00:00', 'Zoom', 'Virtual', 'Programado'),
('Jornada de Emprendimiento', 'Ponencias de emprendedores locales', '2026-10-15 10:00:00', 'Sala de Conferencias B', 'Híbrido', 'Finalizado'),
('Seminario de Bases de Datos', 'MySQL, modelado y optimización de consultas', '2026-11-09 08:30:00', 'Laboratorio 3', 'Presencial', 'En curso'),
('Foro de Software Libre', 'Comunidad y contribución a proyectos open source', '2026-12-05 16:00:00', 'Microsoft Teams', 'Virtual', 'Cancelado');

INSERT INTO participantes (nombres, apellidos, dni, correo, telefono, institucion) VALUES
('Ana Lucía',   'Quispe Rojas',     '70000001', 'ana.quispe@example.com',    '987654321', 'Instituto Tecnológico Cañete'),
('Carlos',      'Mendoza Vega',     '70000002', 'carlos.mendoza@example.com','987654322', 'Instituto Tecnológico Cañete'),
('Rosa',        'Huamán Flores',    '70000003', 'rosa.huaman@example.com',   '987654323', 'Universidad Nacional Andina'),
('Luis',        'Paredes Soto',     '70000004', 'luis.paredes@example.com',  NULL,        'Universidad Nacional Andina'),
('María',       'Castillo Díaz',    '70000005', 'maria.castillo@example.com','987654325', 'Empresa TecnoSur S.A.C.'),
('Jorge',       'Ramírez Salas',    '70000006', 'jorge.ramirez@example.com', '987654326', 'Empresa TecnoSur S.A.C.'),
('Elena',       'Torres Campos',    '70000007', 'elena.torres@example.com',  '987654327', 'Instituto Tecnológico Cañete'),
('Diego',       'Vargas Luna',      '70000008', 'diego.vargas@example.com',  '987654328', 'Colegio San Martín');

INSERT INTO inscripciones (id_evento, id_participante, fecha_inscripcion, estado) VALUES
(1, 1, '2026-11-01 10:15:00', 'Confirmado'),
(1, 2, '2026-11-01 11:20:00', 'Inscrito'),
(1, 3, '2026-11-02 09:05:00', 'Confirmado'),
(1, 5, '2026-11-03 14:40:00', 'Inscrito'),
(2, 2, '2026-11-04 08:30:00', 'Inscrito'),
(2, 6, '2026-11-04 16:10:00', 'Confirmado'),
(3, 4, '2026-10-05 12:00:00', 'Confirmado'),
(3, 7, '2026-10-06 18:25:00', 'Confirmado'),
(4, 1, '2026-11-05 07:50:00', 'Confirmado'),
(4, 8, '2026-11-05 08:10:00', 'Inscrito');
