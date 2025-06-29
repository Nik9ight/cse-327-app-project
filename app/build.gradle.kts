//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//    //id("com.android.application")
//    id("com.google.gms.google-services")
//}
//
//android {
//    namespace = "com.example.myllmapp"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "com.example.myllmapp"
//        minSdk = 24
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//}
//
//dependencies {
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.analytics)
//
//    implementation(libs.firebase.auth)
//
//    implementation(libs.androidx.credentials)
//    implementation(libs.androidx.credentials.play.services.auth)
//    implementation(libs.googleid)
//
//
//
//}


// version from AI
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

//android {
//    namespace = "com.example.llmintegration"
//    compileSdk = 34
//
//    defaultConfig {
//        applicationId = "com.example.llmintegration"
//        minSdk = 24
//        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
//    }
android {
    namespace = "com.example.myllmapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myllmapp"
<<<<<<< Updated upstream
        minSdk = 33
=======
<<<<<<< Updated upstream
        minSdk = 24
=======
        //n
        minSdk = 24 //n
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    androidResources {
        noCompress += "onnx"
    }

    buildFeatures {
        viewBinding=true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("de.kherud:llama:4.2.0")
    implementation(project(":llama"))
<<<<<<< Updated upstream

=======
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(files("libs/onnxruntime-android-1.22.0.aar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


>>>>>>> Stashed changes
>>>>>>> Stashed changes
}
