# 🎮 Proyecto Final | Tienda Gamer "Cyber Store MX"

*Realizado por:*
## Fabian Valencia Muñoz  |  Alan David Gómez López  |  Jacobo Salas Mejía

## 📱 Descripción General del Proyecto
**Cyber Store MX** es una aplicación móvil desarrollada en **AndroidStudio** utilizando el lenguaje **Kotlin** y el framework de interfaz declarativa **Jetpack Compose**.

La aplicación simula una tienda en línea especializada en productos gamer (consolas y PC). Su funcionalidad principal permite al usuario navegar por un catálogo, gestionar un carrito de compras y realizar un pedido utilizando **geolocalización en tiempo real** para determinar la dirección de entrega, finalizando con una firma digital.


## 🔧 Requisitos Técnicos Cumplidos
Este proyecto fue desarrollado siguiendo estrictamente la rúbrica de evaluación:

- [x] **Lenguaje y UI:** Desarrollo 100% en **Kotlin** con **Jetpack Compose**.
- [x] **Funcionalidad Avanzada:** Implementación de **Geolocalización** (GPS) para obtener la ubicación del usuario.
- [x] **Navegación:** Gestión de estados para flujo de pantallas (Menú -> Carrito -> Envío -> Firma).
- [x] **Entrega:** Repositorio público en GitHub y APK funcional.


## 🚀 Instrucciones de Instalación y Ejecución

Para probar la aplicación, elige una de las siguientes opciones:

### Opción A: Instalación Directa (APK)
1.  Descarga el archivo `app-debug.apk` que se encuentra en la lista de archivos de este repositorio.
2.  Transfiere el archivo a tu dispositivo Android.
3.  Instala la aplicación (habilita "Orígenes desconocidos" si se solicita).
4.  **Importante:** Al llegar a la pantalla de envío, concede los permisos de ubicación para probar la funcionalidad GPS.

### Opción B: Ejecución desde Código (Paso a Paso)
Si deseas abrir el código en Android Studio, sigue estos pasos para replicar el proyecto desde cero:

1.  **Crear el Proyecto:**
    * Abre Android Studio y selecciona **New Project**.
    * Elige la plantilla **"Empty Activity"** (asegúrate que sea la que tiene el logo de Compose).
    * En *Name*, escribe: `ProyectoFinal`.
    * En *Package name*, asegúrate que diga: `com.example.proyectofinal` (Importante para que coincida con el código).
    * Haz clic en **Finish** y espera a que cargue.

2.  **Copiar el AndroidManifest.xml:**
    * En Android Studio, navega a `app > manifests > AndroidManifest.xml`.
    * Borra todo su contenido.
    * Copia el código del archivo `AndroidManifest.xml` de este repositorio y pégalo ahí.

3.  **Copiar las Dependencias (build.gradle):**
    * Navega a `Gradle Scripts > build.gradle.kts (Module :app)`.
    * Borra todo y pega el contenido del archivo `build.gradle.kts` de este repositorio.
    * Presiona el botón **"Sync Now"** (elefante con flecha azul) que aparecerá arriba a la derecha.

4.  **Copiar (MainActivity):**
    * Navega a `app > java > com.example.proyectofinal > MainActivity`.
    * Borra todo el contenido.
    * Copia el código del archivo `MainActivity.kt` de este repositorio y pégalo ahí.

5.  **Ejecutar:**
    * Dale al botón de **Play (▶)** verde en la barra superior para lanzar la app en tu emulador o celular conectado.

      
## 🛠 Explicación Técnica del Funcionamiento

### 1. Estructura de Archivos Clave
* **`MainActivity.kt`**: Es el corazón de la app. Contiene toda la lógica de la interfaz (UI) y navegación. Utiliza componentes `Composable` para renderizar las pantallas de Menú, Carrito y Formularios.
* **`AndroidManifest.xml`**: Archivo de configuración esencial. Aquí se declaran los permisos de **Internet** y **Ubicación Precisa (ACCESS_FINE_LOCATION)** necesarios para el funcionamiento del módulo de envíos.
* **`build.gradle.kts`**: Gestiona las dependencias del proyecto, incluyendo las librerías de *Material Design 3* y *Google Play Services Location*.

### 2. Flujo de Datos y Navegación
La app utiliza un patrón de navegación por estados (`State Handling`):
1.  **Pantalla Menú:** Muestra una lista de productos (`LazyColumn`).
2.  **Pantalla Carrito:** Calcula el total dinámicamente.
3.  **Pantalla Envío (Geolocalización):**
    * Verifica permisos en tiempo real.
    * Obtiene latitud/longitud del sensor GPS.
    * Usa `Geocoder` para convertir coordenadas en una dirección legible.
4.  **Pantalla Firma:** Captura trazos en un `Canvas` para autorizar la compra.

### 3. Dependencias Principales
* `androidx.compose.material3`: Para el diseño visual moderno.
* `com.google.android.gms:play-services-location`: Para la funcionalidad de GPS.
* `androidx.activity.compose`: Para integrar actividades con Compose.

