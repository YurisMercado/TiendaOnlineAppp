plugins {
<<<<<<< HEAD
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
=======
    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        id("org.jetbrains.kotlin.kapt")
    }

>>>>>>> 2cb6c7d79cc2113dfaeb30ed1f6391e2bf9f5d8c
}

android {
    namespace = "com.tiendaonlineapp"
<<<<<<< HEAD
    compileSdk = 34
=======
    compileSdk {
        version = release(36)
    }
>>>>>>> 2cb6c7d79cc2113dfaeb30ed1f6391e2bf9f5d8c

    defaultConfig {
        applicationId = "com.tiendaonlineapp"
        minSdk = 26
<<<<<<< HEAD
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
=======
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
>>>>>>> 2cb6c7d79cc2113dfaeb30ed1f6391e2bf9f5d8c
    }
}

dependencies {
<<<<<<< HEAD

    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //GEOLOCALIZACIÓN
    implementation("com.google.android.gms:play-services-location:21.0.1")


    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
=======
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    
    // Room database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    
    // Lifecycle components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    
    // Google Play Services Location
    implementation(libs.play.services.location)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
>>>>>>> 2cb6c7d79cc2113dfaeb30ed1f6391e2bf9f5d8c
}