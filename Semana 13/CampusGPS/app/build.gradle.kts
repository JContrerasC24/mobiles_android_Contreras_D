import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// =====================================================
// CONFIGURACIÓN DE LA CLAVE DE GOOGLE MAPS
// =====================================================

// Permite leer las propiedades guardadas en local.properties.
val localProps = Properties().apply {

    // Busca local.properties en la carpeta principal de CampusGPS.
    val archivo = rootProject.file("local.properties")

    // Solo intenta leerlo si existe.
    if (archivo.exists()) {
        archivo.inputStream().use { input ->
            load(input)
        }
    }
}

android {

    // Paquete principal de nuestra aplicación.
    namespace = "com.contreras.campusgps"

    compileSdk {
        version = release(37)
    }

    defaultConfig {

        applicationId = "com.contreras.campusgps"

        minSdk = 24
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        // =================================================
        // CLAVE DE GOOGLE MAPS
        // =================================================

        // Obtiene MAPS_API_KEY desde local.properties.
        // Su valor se utilizará en AndroidManifest.xml.
        manifestPlaceholders["MAPS_API_KEY"] =
            localProps.getProperty("MAPS_API_KEY", "")
    }

    buildTypes {

        release {

            optimization {
                enable = false
            }
        }
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11

        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {

        // Habilita la creación de interfaces con Jetpack Compose.
        compose = true
    }
}

dependencies {

    // =====================================================
    // DEPENDENCIAS PRINCIPALES DE ANDROID
    // =====================================================

    implementation(
        platform(libs.androidx.compose.bom)
    )

    implementation(
        libs.androidx.activity.compose
    )

    implementation(
        libs.androidx.compose.material3
    )

    implementation(
        libs.androidx.compose.ui
    )

    implementation(
        libs.androidx.compose.ui.graphics
    )

    implementation(
        libs.androidx.compose.ui.tooling.preview
    )

    implementation(
        libs.androidx.core.ktx
    )

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    // =====================================================
    // GOOGLE PLAY SERVICES - UBICACIÓN
    // =====================================================

    // Permite obtener la ubicación del dispositivo mediante GPS,
    // Wi-Fi y otras fuentes disponibles.
    implementation(
        "com.google.android.gms:play-services-location:21.3.0"
    )

    // =====================================================
    // CORRUTINAS PARA GOOGLE PLAY SERVICES
    // =====================================================

    // Permite esperar resultados de ubicación mediante await()
    // sin bloquear la interfaz de la aplicación.
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1"
    )

    // =====================================================
    // GOOGLE MAPS PARA JETPACK COMPOSE
    // =====================================================

    // Permite mostrar un mapa interactivo, agregar marcadores
    // y controlar la cámara mediante código Kotlin.
    //
    // Mantenemos la versión 7.0.0 por compatibilidad con
    // la configuración de Kotlin de nuestro proyecto.
    implementation(
        "com.google.maps.android:maps-compose:7.0.0"
    )

    // =====================================================
    // VIEWMODEL PARA JETPACK COMPOSE
    // =====================================================

    // Permite conectar las pantallas Compose con los ViewModels
    // siguiendo la arquitectura MVVM del laboratorio.
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    )

    // =====================================================
    // DEPENDENCIAS PARA PRUEBAS
    // =====================================================

    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    // =====================================================
    // DEPENDENCIAS PARA DEPURACIÓN
    // =====================================================

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}