# 🎵 PlaySound

Este es un proyecto de aplicación de música para Android desarrollado en Java. Permite a los usuarios disfrutar de su biblioteca musical local con diversas funciones como reproducción, listas personalizadas y exploración por álbumes o artistas.

## Estado del Proyecto 
En Desarrollo

## Tecnologías usadas
- Java
- XML
- SQL
- Gradlew

## Organizacion del Proyecto
```
src/
└── main/
    ├── AndroidManifest.xml          # Archivo principal de configuración de la aplicación Android
    ├── assets/
    │   ├── db_listMusic.databases   # Base de datos SQLite de la aplicación
    │   └── db.sql                   # Script SQL para crear la base de datos
    ├── java/
    │   └── com/
    │       └── juanpablogarzon/
    │           ├── reproductor_movil/
    │           │   ├── Album.java          # Archivo para vista de álbumes
    │           │   ├── App.java            # Archivo principal, punto de inicio
    │           │   ├── Artist.java         # Archivo para vista de artistas y sus canciones
    │           │   ├── List.java           # Archivo para vista de una lista en particular
    │           │   ├── Lists.java          # Archivo para vista de todas las listas
    │           │   ├── Reproductor.java    # Archivo del reproductor de música
    │           │   └── Search.java         # Archivo para resultados de búsqueda
    │           └── utils/
    │               └── MetodosCompartidos.java  # Métodos compartidos usados en varias clases
    └── res/
        ├── drawable/              # Imágenes y gráficos usados en la app
        └── layout/                # Archivos XML que definen la interfaz gráfica
            ├── activity_album.xml            # XML para vista de álbumes
            ├── activity_artist.xml           # XML para vista de artistas
            ├── activity_list_reproduction.xml # XML para vista de listas de reproducción
            ├── activity_list.xml             # XML para vista de una lista en particular
            ├── activity_main.xml             # XML para pantalla principal / inicio
            ├── activity_playmusic.xml        # XML para reproductor de música
            └── activity_search.xml           # XML para resultados de búsqueda

```

---

## 📱 Características

- 🎧 Reproducción de música local
- 📁 Creación y gestión de listas de reproducción
- 🔎 Búsqueda de canciones
- 🧑‍🎤 Exploración por artistas y álbumes
- 💾 Base de datos local usando SQLite
- 🖼️ Interfaz creada con XML
- ⚙️ Sistema de construcción Gradle

---

### Instalación

```bash
git clone https://github.com/JuanPabloGarzonOrtiz/PlaySound.git