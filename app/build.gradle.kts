plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id("com.google.gms.google-services")
    id("kotlin-kapt")

}

android {
    namespace = "com.example.quanlyamthuc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quanlyamthuc"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.squareup.picasso:picasso:2.8")

    // Google Identity Services (Sign In With Google)
    implementation(libs.googleid)

    implementation ("com.github.bumptech.glide:glide:4.16.0")
   // annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
 kapt("com.github.bumptech.glide:compiler:4.16.0") // ✅ ĐÚNG


    //image slide
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
}