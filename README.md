# Master Martini · App de contenidos técnicos

Aplicación Android nativa desarrollada para la **Evaluación Parcial 2** de **DSY1105 Desarrollo de Aplicaciones Móviles** (DUOC UC), basada en el caso **Master Martini Chile SPA**.

Master Martini es una comercializadora de insumos para alimentos que busca fortalecer la relación con clientes y prospectos mediante contenidos de valor. Esta app entrega **recetas, videos técnicos y cursos** a emprendedores y profesionales gastronómicos (panaderías, pastelerías, chocolaterías, casinos y restaurantes). Registra el perfil del negocio y sus intereses para **recomendar contenidos** y mantener información segmentada de los usuarios.

La interfaz está pensada para **usuarios mayores de 50 años con distintos niveles de manejo digital**: letra grande, alto contraste, opciones fáciles de tocar y mensajes de error claros con ejemplos.

> MVP académico: todos los datos (usuarios, contenidos, chefs) son **ficticios**. No se usan credenciales ni datos personales reales.

---

## Integrantes

| Integrante |
|---|
| Nicolás Gabriel Aldea Fernández |
| Vicente Agustín González Pinto |

**Equipo:** 4 · **Sección:** 003V

---

## Funcionalidades implementadas

**Bienvenida y sesión**
- Pantalla de bienvenida animada que reconoce si ya existe un perfil ("Hola de nuevo, …").

**Formularios con validación**
- **Registro**: nombre, correo, teléfono, dirección, RUT de empresa, tipo y tamaño de negocio.
- **Encuesta de intereses**: especialidades, formato preferido, nivel de experiencia y aviso de novedades. Se puede editar después desde el perfil.
- Cada campo tiene ícono, check de campo correcto y mensaje de error animado.
- El RUT se valida con **dígito verificador (módulo 11)**.

**Catálogo**
- Saludo personalizado y filtros por especialidad.
- Sección **"Recomendado para ti"** según la encuesta.
- Detalle de cada contenido con duración, nivel, chef y pasos numerados.
- Los videos se muestran como miniatura y se abren con **"Ver video"**.

**Favoritos**
- Corazón animado para guardar contenidos y pestaña propia de favoritos.

**Perfil**
- Datos del negocio y de contacto, intereses y cantidad de favoritos.
- **Foto de perfil** desde la **cámara** o la **galería**.

**Navegación y animaciones**
- Menú inferior animado (Contenidos · Favoritos · Perfil).
- Transiciones entre pantallas y **animación de carga** al entrar a cada sección y al guardar.

**Persistencia local**
- Todo queda guardado en el teléfono con **Room (SQLite)**: perfil, intereses, favoritos y foto.

---

## Recursos nativos y permisos

| Recurso | Uso | Permiso |
|---|---|---|
| Cámara | Tomar la foto de perfil | `CAMERA`, solicitado al momento de usarla |
| Galería de fotos | Elegir la foto de perfil | `READ_MEDIA_IMAGES` / `READ_MEDIA_VISUAL_USER_SELECTED` (Android 13+) o `READ_EXTERNAL_STORAGE` (Android 12 o inferior), solicitado al momento de usarla |

Si el usuario rechaza un permiso, la app muestra un mensaje y sigue funcionando. La foto se guarda en el almacenamiento interno y privado de la app.

---

## Tecnologías

| Tecnología | Uso |
|---|---|
| Kotlin 2.0 | Lenguaje |
| Jetpack Compose + Material 3 | Interfaz |
| MVVM (ViewModel + StateFlow) | Arquitectura y manejo de estado |
| Room 2.6 + KSP | Persistencia local (SQLite) |
| Navigation Compose | Navegación entre pantallas |
| Coroutines + Flow | Operaciones asíncronas y datos reactivos |
| JUnit | Tests unitarios de validación |

- compileSdk 36 · minSdk 24 (**Android 7.0 o superior**)
- Android Gradle Plugin 8.12.3 · Gradle 8.13 · JDK 17 o 21

---

## Arquitectura (MVVM)

```
Pantalla (Compose) → ViewModel (StateFlow) → Repository → DAO → Room (SQLite)
```

- Las **pantallas** solo muestran el estado y envían las acciones del usuario.
- Los **ViewModels** contienen la lógica y validan usando `Validadores`.
- El **Repository** es el único punto de acceso a los datos. Esto permite, en una etapa futura, conectar la app con la plataforma web de Master Martini sin cambiar las pantallas.

## Estructura del proyecto

```
app/src/main/java/com/example/master_martini/
├── data/
│   ├── local/
│   │   ├── model/       # Listas fijas: especialidades, tipos de contenido y de negocio
│   │   ├── entity/      # Tablas de Room
│   │   ├── dao/         # Consultas a la base de datos
│   │   ├── database/    # Base de datos Room
│   │   ├── seed/        # Contenidos ficticios iniciales
│   │   └── AlmacenFotos.kt
│   └── repository/      # Acceso único a los datos
├── validation/          # Reglas de validación (con tests)
├── viewmodel/           # Estado y lógica de cada pantalla
├── ui/
│   ├── components/      # Componentes reutilizables y animaciones
│   ├── screens/         # Pantallas
│   └── theme/           # Colores y tipografía accesibles
├── navigation/          # Rutas y menú inferior
├── MasterMartiniApp.kt
└── MainActivity.kt
```

---

## Cómo ejecutar el proyecto

**Requisitos**
- Android Studio reciente (2025 o superior).
- JDK 17 o 21 (Android Studio incluye uno: *jbr-21*).
- Emulador o celular con **Android 7.0 o superior**.

**Pasos**
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Vvcentek/Proyecto1_AppMoviles.git
   ```
2. Abrir la carpeta en Android Studio y esperar el **Gradle Sync**.
3. Si se pide, configurar *Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK* en **jbr-21** (o JDK 17).
4. Elegir la configuración **app** y un dispositivo (emulador o celular con depuración USB).
5. Presionar **Run ▶**.

Desde la terminal:
```bash
./gradlew assembleDebug        # Compila la app
./gradlew testDebugUnitTest    # Ejecuta los tests de validación
```

---

## Notas

- Contenidos, chefs y datos de usuarios: ficticios, creados para el prototipo académico.
- Paleta de colores provisoria, mientras se reciben los colores corporativos de Master Martini.