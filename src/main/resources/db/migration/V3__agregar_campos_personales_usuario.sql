-- ============================================
-- V3: Agregar campos personales a usuarios
-- ============================================

-- Agregar campos de datos personales a la tabla usuarios
ALTER TABLE usuarios
    ADD COLUMN nombre VARCHAR(100),
    ADD COLUMN apellido VARCHAR(100),
    ADD COLUMN cedula VARCHAR(20),
    ADD COLUMN telefono VARCHAR(20),
    ADD COLUMN fecha_nacimiento DATE,
    ADD COLUMN direccion TEXT;

-- Migrar datos existentes de profesores a usuarios
UPDATE usuarios u
SET
    nombre = p.nombre,
    apellido = p.apellido,
    cedula = p.cedula,
    telefono = p.telefono
FROM profesores p
WHERE u.id = p.usuario_id;

-- Migrar datos existentes de estudiantes a usuarios
UPDATE usuarios u
SET
    nombre = COALESCE(u.nombre, e.nombre),
    apellido = COALESCE(u.apellido, e.apellido),
    cedula = COALESCE(u.cedula, e.cedula),
    telefono = COALESCE(u.telefono, e.telefono),
    fecha_nacimiento = e.fecha_nacimiento,
    direccion = e.direccion
FROM estudiantes e
WHERE u.id = e.usuario_id;

-- Para usuarios que no son ni profesor ni estudiante, poner datos por defecto
UPDATE usuarios
SET
    nombre = COALESCE(nombre, 'Sin'),
    apellido = COALESCE(apellido, 'Asignar'),
    cedula = COALESCE(cedula, 'PENDING-' || id::text)
WHERE nombre IS NULL;

-- Ahora hacer los campos obligatorios (NOT NULL)
ALTER TABLE usuarios
    ALTER COLUMN nombre SET NOT NULL,
    ALTER COLUMN apellido SET NOT NULL,
    ALTER COLUMN cedula SET NOT NULL;

-- Agregar constraint de unicidad para cedula
ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuarios_cedula UNIQUE (cedula);

-- Eliminar campos duplicados de profesores
ALTER TABLE profesores
    DROP COLUMN IF EXISTS nombre,
    DROP COLUMN IF EXISTS apellido,
    DROP COLUMN IF EXISTS cedula,
    DROP COLUMN IF EXISTS telefono;

-- Eliminar campos duplicados de estudiantes
ALTER TABLE estudiantes
    DROP COLUMN IF EXISTS nombre,
    DROP COLUMN IF EXISTS apellido,
    DROP COLUMN IF EXISTS cedula,
    DROP COLUMN IF EXISTS telefono,
    DROP COLUMN IF EXISTS fecha_nacimiento,
    DROP COLUMN IF EXISTS direccion;

-- Comentarios
COMMENT ON COLUMN usuarios.nombre IS 'Nombre de la persona';
COMMENT ON COLUMN usuarios.apellido IS 'Apellido de la persona';
COMMENT ON COLUMN usuarios.cedula IS 'Cedula de identidad unica';
COMMENT ON COLUMN usuarios.telefono IS 'Numero de telefono';
COMMENT ON COLUMN usuarios.fecha_nacimiento IS 'Fecha de nacimiento';
COMMENT ON COLUMN usuarios.direccion IS 'Direccion de residencia';