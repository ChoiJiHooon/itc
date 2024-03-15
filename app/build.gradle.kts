
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    buildFeatures{
        viewBinding = true
    }

    aaptOptions {
        noCompress ("tflite")
    }
    namespace = "com.example.itc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.itc"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("androidx.exifinterface:exifinterface:1.3.2")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.android.identity:identity-credential-android:20231002")
    implementation("com.naver.maps:map-sdk:3.17.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:3.11.0")
    implementation("com.navercorp.nid:oauth:5.9.0") // jdk 11
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation ("androidx.legacy:legacy-support-core-utils:1.0.0")
    implementation ("androidx.browser:browser:1.4.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
    implementation ("androidx.fragment:fragment-ktx:1.3.6")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:retrofit-converters:2.5.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.5.0")
    implementation ("com.squareup.retrofit2:adapter-rxjava:2.1.0")
    implementation ("com.squareup.moshi:moshi-kotlin:1.11.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation ("com.airbnb.android:lottie:3.1.0")
    implementation("androidx.media3:media3-common:1.2.0")
    implementation ("com.kakao.sdk:v2-all:2.17.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation ("com.kakao.sdk:v2-user:2.17.0") // 카카오 로그인
    implementation ("com.kakao.sdk:v2-talk:2.17.0") // 친구, 메시지(카카오톡)
    implementation ("com.kakao.sdk:v2-story:2.17.0") // 카카오스토리
    implementation ("com.kakao.sdk:v2-share:2.17.0") // 메시지(카카오톡 공유)
    implementation ("com.kakao.sdk:v2-friend:2.17.0") // 카카오톡 소셜 피커, 리소스 번들 파일 포함
    implementation ("com.kakao.sdk:v2-navi:2.17.0") // 카카오내비
    implementation ("com.kakao.sdk:v2-cert:2.17.0") // 카카오 인증서비스
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("org.pytorch:pytorch_android_lite:+")
    implementation ("org.pytorch:pytorch_android_torchvision_lite:+")
    implementation ("org.tensorflow:tensorflow-lite:+")
    implementation ("org.tensorflow:tensorflow-lite-task-vision:+")
    implementation ("androidx.camera:camera-core:1.1.0-beta01")
    implementation ("androidx.camera:camera-camera2:1.1.0-beta01")
    implementation ("androidx.camera:camera-lifecycle:1.1.0-beta01")
    implementation ("androidx.camera:camera-video:1.1.0-beta01")
    implementation ("androidx.camera:camera-view:1.0.0-alpha17")
    implementation ("androidx.camera:camera-extensions:1.1.0-beta01")
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")
    implementation("com.google.mlkit:image-labeling-default-common:17.0.0")
    implementation("de.hdodenhof:circleimageview:2.2.0")
}