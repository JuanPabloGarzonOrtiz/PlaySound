CREATE TABLE listas(
	id_lista INTEGER PRIMARY KEY,
	nombre VARCHAR(50) NOT NULL
);
CREATE TABLE canciones(
	id_Cancion INTEGER PRIMARY KEY,
	nombre VARCHAR(234) NOT NULL
);
CREATE TABLE relaciones(
	id_relacion INTEGER PRIMARY KEY,
	id_Cancion INTEGER NOT NULL,
	id_lista INTEGER,
	FOREIGN KEY(id_Cancion) REFERENCES canciones(id_Cancion),
	FOREIGN KEY(id_lista) REFERENCES listas(id_lista)
);
CREATE TABLE historial_canciones(
	id_history INTEGER PRIMARY KEY,
	nombre_Song VARCHAR(234) NOT NULL
);
INSERT INTO listas (nombre) 
	VALUES 
		('Me Gusta');