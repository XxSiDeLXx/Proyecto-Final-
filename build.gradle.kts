
// 1. PLUGINS
// Herramientas necesarias para que el proyecto compile con Android, Kotlin y Compose.

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Plugin oficial para Jetpack Compose
}


// 2. CONFIGURACIÓN DE ANDROID
// Define las versiones del sistema operativo y las características de compilación.

android {
    namespace = "com.example.proyectofinal"
    compileSdk = 36 // Versión del SDK con la que se compila la app

    defaultConfig {
        applicationId = "com.example.proyectofinal"
        // MinSdk 24: La app funcionará desde Android 7.0 (Nougat) en adelante
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Compatibilidad con Java 11 (Requerido por las nuevas versiones de Android Studio)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // ACTIVACIÓN DE JETPACK COMPOSE (Requisito Obligatorio)
    // Habilita el nuevo kit de herramientas de UI declarativa.
    buildFeatures {
        compose = true
    }
}


// 3. DEPENDENCIAS (LIBRERÍAS)
// Aquí se agregan las herramientas externas que usa la app.
dependencies {

    //  LIBRERÍAS DEL NÚCLEO DE ANDROID
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    //  LIBRERÍAS DE INTERFAZ GRÁFICA (JETPACK COMPOSE)
    // BOM (Bill of Materials) gestiona las versiones de todas las librerías de Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3) // Material Design 3 (Estilos modernos)

    //  LIBRERÍAS PARA TESTING (PRUEBAS)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    //  HERRAMIENTAS DE DEPURACIÓN
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    // REQUISITO DE GEOLOCALIZACIÓN
    implementation("androidx.core:core-ktx:1.12.0")
    // Librería de Google Play Services para obtener la ubicación (GPS/Red)
    implementation("com.google.android.gms:play-services-location:21.0.1")
}