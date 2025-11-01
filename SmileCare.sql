-- Script - Proyecto cátedra
CREATE DATABASE SmileCare;

-- Eliminar tablas existentes si es necesario
DROP TABLE IF EXISTS recordatorios;
DROP TABLE IF EXISTS citas;
DROP TABLE IF EXISTS disponibilidades;
DROP TABLE IF EXISTS doctores;
DROP TABLE IF EXISTS usuarios;

-- Tabla de usuarios 
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    firebase_uid TEXT UNIQUE NOT NULL,
    nombre TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    telefono TEXT,
    rol TEXT DEFAULT 'paciente' CHECK(rol IN ('paciente', 'admin', 'doctor')),
    estado TEXT DEFAULT 'activo' CHECK(estado IN ('activo', 'inactivo')),
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE doctores

-- Tabla de doctores
CREATE TABLE doctores (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    especialidad TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    telefono TEXT NOT NULL,
    descripcion TEXT,
    foto_url TEXT,
    estado TEXT DEFAULT 'activo' CHECK(estado IN ('activo', 'inactivo')),
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de disponibilidades
CREATE TABLE disponibilidades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    doctor_id INTEGER NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    tipo TEXT DEFAULT 'normal' CHECK(tipo IN ('normal', 'bloqueo')),
    estado TEXT DEFAULT 'disponible' CHECK(estado IN ('disponible', 'ocupado', 'bloqueado')),
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctores (id) ON DELETE CASCADE,
    UNIQUE(doctor_id, fecha, hora_inicio)
);

-- Tabla de citas
CREATE TABLE citas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    paciente_id TEXT NOT NULL, -- Cambiado a TEXT para Firebase UID
    doctor_id INTEGER NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado TEXT DEFAULT 'pendiente' CHECK(estado IN ('pendiente', 'confirmada', 'cancelada', 'atendida')),
    motivo TEXT,
    ubicacion TEXT DEFAULT 'Clínica Dental SmileCare, Calle Principal #123, San Salvador',
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctores (id) ON DELETE CASCADE
);

-- Tabla de recordatorios
CREATE TABLE recordatorios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cita_id INTEGER NOT NULL,
    tipo TEXT CHECK(tipo IN ('24h', '1h')),
    enviado BOOLEAN DEFAULT 0,
    enviado_en DATETIME,
    FOREIGN KEY (cita_id) REFERENCES citas (id) ON DELETE CASCADE
);

-- Índices para mejor rendimiento
CREATE INDEX idx_disponibilidades_doctor_fecha ON disponibilidades(doctor_id, fecha);
CREATE INDEX idx_citas_paciente_estado ON citas(paciente_id, estado);
CREATE INDEX idx_citas_doctor_fecha ON citas(doctor_id, fecha);
CREATE INDEX idx_usuarios_firebase_uid ON usuarios(firebase_uid);

-- Insertar doctores 
INSERT INTO doctores (id, nombre, especialidad, email, telefono, descripcion, foto_url, estado) VALUES
(1, 'Dr. Carlos Martínez', 'Ortodoncia', 'carlos.martinez@smilecare.com', '2222-1111', 'Especialista en ortodoncia con 10 años de experiencia', 'https://example.com/doctor1.jpg', 'activo'),
(2, 'Dra. Ana García', 'Periodoncia', 'ana.garcia@smilecare.com', '2222-1112', 'Especialista en enfermedades de las encías', 'https://example.com/doctor2.jpg', 'activo'),
(3, 'Dr. José López', 'Endodoncia', 'jose.lopez@smilecare.com', '2222-1113', 'Especialista en tratamientos de conducto', 'https://example.com/doctor3.jpg', 'activo'),
(4, 'Dra. María Rodríguez', 'Odontopediatría', 'maria.rodriguez@smilecare.com', '2222-1114', 'Especialista en odontología para niños', 'https://example.com/doctor4.jpg', 'activo'),
(5, 'Dr. Roberto Hernández', 'Implantes Dentales', 'roberto.hernandez@smilecare.com', '2222-1115', 'Especialista en implantes dentales', 'https://example.com/doctor5.jpg', 'activo'),
(6, 'Dra. Sofía Vargas', 'Estética Dental', 'sofia.vargas@smilecare.com', '2222-1116', 'Especialista en blanqueamiento y carillas', 'https://example.com/doctor6.jpg', 'activo'),
(7, 'Dr. Miguel Torres', 'Cirugía Oral', 'miguel.torres@smilecare.com', '2222-1117', 'Especialista en cirugías bucales', 'https://example.com/doctor7.jpg', 'activo'),
(8, 'Dra. Laura Jiménez', 'Prótesis Dental', 'laura.jimenez@smilecare.com', '2222-1118', 'Especialista en prótesis y coronas', 'https://example.com/doctor8.jpg', 'activo');

-- Doctor 1 (Carlos Martínez)
INSERT INTO disponibilidades (doctor_id, fecha, hora_inicio, hora_fin) VALUES
(1, '2025-11-04', '08:00', '09:00'), (1, '2025-11-04', '09:00', '10:00'), (1, '2025-11-04', '10:00', '11:00'),
(1, '2025-11-05', '08:00', '09:00'), (1, '2025-11-05', '14:00', '15:00'), (1, '2025-11-05', '15:00', '16:00'),
(1, '2025-11-06', '09:00', '10:00'), (1, '2025-11-06', '10:00', '11:00'), (1, '2025-11-06', '16:00', '17:00');

-- Doctor 2 (Ana García)
INSERT INTO disponibilidades (doctor_id, fecha, hora_inicio, hora_fin) VALUES
(2, '2025-11-04', '08:00', '09:00'), (2, '2025-11-04', '09:00', '10:00'), (2, '2025-11-04', '10:00', '11:00'),
(2, '2025-11-05', '08:00', '09:00'), (2, '2025-11-05', '14:00', '15:00'), (2, '2025-11-05', '15:00', '16:00'),
(2, '2025-11-06', '09:00', '10:00'), (2, '2025-11-06', '10:00', '11:00'), (2, '2025-11-06', '16:00', '17:00');

-- Doctor 3 (José López)
INSERT INTO disponibilidades (doctor_id, fecha, hora_inicio, hora_fin) VALUES
(3, '2025-11-04', '08:00', '09:00'), (3, '2025-11-04', '09:00', '10:00'), (3, '2025-11-04', '10:00', '11:00'),
(3, '2025-11-05', '08:00', '09:00'), (3, '2025-11-05', '14:00', '15:00'), (3, '2025-11-05', '15:00', '16:00'),
(3, '2025-11-06', '09:00', '10:00'), (3, '2025-11-06', '10:00', '11:00'), (3, '2025-11-06', '16:00', '17:00');

-- Doctor 4 (María Rodríguez)
INSERT INTO disponibilidades (doctor_id, fecha, hora_inicio, hora_fin) VALUES
(4, '2025-11-04', '08:00', '09:00'), (4, '2025-11-04', '09:00', '10:00'), (4, '2025-11-04', '10:00', '11:00'),
(4, '2025-11-05', '08:00', '09:00'), (4, '2025-11-05', '14:00', '15:00'), (4, '2025-11-05', '15:00', '16:00'),
(4, '2025-11-06', '09:00', '10:00'), (4, '2025-11-06', '10:00', '11:00'), (4, '2025-11-06', '16:00', '17:00');

-- Doctor 5 (Roberto Hernández)
INSERT INTO disponibilidades (doctor_id, fecha, hora_inicio, hora_fin) VALUES
(5, '2025-11-04', '08:00', '09:00'), (5, '2025-11-04', '09:00', '10:00'), (5, '2025-11-04', '10:00', '11:00'),
(5, '2025-11-05', '08:00', '09:00'), (5, '2025-11-05', '14:00', '15:00'), (5, '2025-11-05', '15:00', '16:00'),
(5, '2025-11-06', '09:00', '10:00'), (5, '2025-11-06', '10:00', '11:00'), (5, '2025-11-06', '16:00', '17:00');

-- Doctor 6 (Sofía Vargas)
INSERT INTO disponibilidades (doctor_id, fecha, hora_inicio, hora_fin) VALUES
(6, '2025-11-04', '08:00', '09:00'), (6, '2025-11-04', '09:00', '10:00'), (6, '2025-11-04', '10:00', '11:00'),
(6, '2025-11-05', '08:00', '09:00'), (6, '2025-11-05', '14:00', '15:00'), (6, '2025-11-05', '15:00', '16:00'),
(6, '2025-11-06', '09:00', '10:00'), (6, '2025-11-06', '10:00', '11:00'), (6, '2025-11-06', '16:00', '17:00');

-- Doctor 7 (Miguel Torres)
INSERT INTO disponibilidades (doctor_id, fecha, hora_inicio, hora_fin) VALUES
(7, '2025-11-04', '08:00', '09:00'), (7, '2025-11-04', '09:00', '10:00'), (7, '2025-11-04', '10:00', '11:00'),
(7, '2025-11-05', '08:00', '09:00'), (7, '2025-11-05', '14:00', '15:00'), (7, '2025-11-05', '15:00', '16:00'),
(7, '2025-11-06', '09:00', '10:00'), (7, '2025-11-06', '10:00', '11:00'), (7, '2025-11-06', '16:00', '17:00');

-- Doctor 8 (Laura Jiménez)
INSERT INTO disponibilidades (doctor_id, fecha, hora_inicio, hora_fin) VALUES
(8, '2025-11-04', '08:00', '09:00'), (8, '2025-11-04', '09:00', '10:00'), (8, '2025-11-04', '10:00', '11:00'),
(8, '2025-11-05', '08:00', '09:00'), (8, '2025-11-05', '14:00', '15:00'), (8, '2025-11-05', '15:00', '16:00'),
(8, '2025-11-06', '09:00', '10:00'), (8, '2025-11-06', '10:00', '11:00'), (8, '2025-11-06', '16:00', '17:00');