plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.contreras.saludplus.citas"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.contreras.saludplus.citas"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        // Permite utilizar java.time desde Android 24.
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        // Habilita las interfaces con Jetpack Compose.
        compose = true
    }
}

dependencies {
    // Mantiene versiones compatibles entre las bibliotecas Compose.
    implementation(platform(libs.androidx.compose.bom))

    // Proporciona los componentes de interfaz.
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Proporciona utilidades de Android y ciclo de vida.
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Habilita la navegación entre pantallas.
    implementation(libs.androidx.navigation.compose)

    // Añade iconos para las pantallas y el menú.
    implementation("androidx.compose.material:material-icons-extended")

    // Proporciona compatibilidad para LocalDate en versiones antiguas.
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // Permite ejecutar pruebas unitarias.
    testImplementation(libs.junit)

    // Proporciona herramientas para pruebas en dispositivos.
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Habilita herramientas de inspección durante el desarrollo.
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}