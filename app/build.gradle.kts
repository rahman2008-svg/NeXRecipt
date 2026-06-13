plugins {
    alias(libs.plugins.android.application)

    // Kotlin plugins
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    // KSP (must be after Kotlin plugins)
    alias(libs.plugins.google.devtools.ksp)

    // Optional tools
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.aistudio.nexreceipt.pro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.nexreceipt.pro"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
                ?: "${rootDir}/my-upload-key.jks"

            storeFile = file(keystorePath)
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = "upload"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isCrunchPngs = false
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            // default debug config
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

/**
 * Secrets plugin config (CI-safe)
 */
secrets {
    propertiesFileName = ".env"
    defaultPropertiesFileName = ".env.example"
}
