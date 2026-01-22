-- Crear tabla de Horarios
CREATE TABLE horarios (
    id BIGSERIAL PRIMARY KEY,
    curso_id BIGINT NOT NULL,
    dia VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    aula VARCHAR(50),
    CONSTRAINT fk_horario_curso FOREIGN KEY (curso_id) REFERENCES cursos(id)
);

-- Crear tabla de Asistencias
CREATE TABLE asistencias (
    id BIGSERIAL PRIMARY KEY,
    inscripcion_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    presente BOOLEAN NOT NULL,
    observaciones VARCHAR(255),
    CONSTRAINT fk_asistencia_inscripcion FOREIGN KEY (inscripcion_id) REFERENCES inscripciones(id),
    CONSTRAINT uk_asistencia_inscripcion_fecha UNIQUE (inscripcion_id, fecha)
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_horario_curso ON horarios(curso_id);
CREATE INDEX idx_asistencia_inscripcion ON asistencias(inscripcion_id);
CREATE INDEX idx_asistencia_fecha ON asistencias(fecha);