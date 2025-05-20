plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // 'org.jetbrains.kotlin.plugin.compose'와 같은 Kotlin Compose 플러그인을 의미할 수 있습니다.
    // libs.versions.toml에 정의된 방식에 따라 다를 수 있습니다.
}

android {
    namespace = "com.example.daytogether"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.daytogether"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Vector Drawable 사용을 위한 설정 (SVG를 XML로 변환하여 사용할 때 필요)
        vectorDrawables {
            useSupportLibrary = true
        }
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // composeOptions { // 필요시 Kotlin Compiler Extension 버전 명시 (Compose BOM이 관리해주는 경우가 많음)
    //     kotlinCompilerExtensionVersion = "1.5.1" // 예시 버전, 사용하는 Compose 버전에 맞는 버전 확인
    // }
    packagingOptions { // 리소스 중복 문제 발생 시 필요할 수 있음
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.coil.compose)

    // 1. Compose BOM (Bill of Materials) - Compose 라이브러리 버전 관리를 용이하게 함
    implementation(platform(libs.androidx.compose.bom)) // libs.versions.toml에 정의된 'androidx-compose-bom'
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // 2. Core Compose 관련 라이브러리들
    implementation(libs.androidx.material3) // Material Design 3 컴포넌트
    implementation("androidx.compose.material:material-icons-extended") // 확장된 Material 아이콘
    implementation("androidx.compose.foundation:foundation") // 기본적인 Compose 구성 요소 및 레이아웃
    implementation(libs.androidx.ui) // Compose UI 기본
    implementation(libs.androidx.ui.graphics) // Compose 그래픽 관련
    implementation(libs.androidx.ui.tooling.preview) // @Preview 어노테이션 지원

    // Navigation
    val navVersion = "2.7.7" // 사용하시는 버전으로 유지
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Accompanist Pager & Indicators (온보딩 화면의 스와이프 기능 및 페이지 표시기)
    // 주의: Accompanist 라이브러리는 점차 공식 라이브러리로 기능이 이전될 수 있으므로,
    // 최신 문서를 확인하고 프로젝트의 Compose 버전과의 호환성을 고려하세요.
    // 2025년 5월 기준 안정적인 버전 중 하나를 사용합니다.
    // 필요시 libs.versions.toml에 버전을 정의하고 alias로 사용할 수 있습니다.
    val accompanistVersion = "0.34.0" // 예시 버전, 최신 안정 버전으로 확인하세요. (또는 Compose 버전에 더 적합한 버전)
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")

    // Core 라이브러리 및 기타 AndroidX 라이브러리
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4") // Java 8+ API Desugaring
    implementation(libs.androidx.core.ktx) // Kotlin 확장 함수
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle KTX
    implementation(libs.androidx.activity.compose) // ComponentActivity.setContent 등 Compose 지원

    // ViewModel Compose 지원 라이브러리 (viewModel() 헬퍼 함수 사용을 위해 추가)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0") // 현재 알려주신 버전 유지 (또는 최신 안정 버전)

    // 테스트 관련 라이브러리
    testImplementation(libs.junit) // JUnit4
    androidTestImplementation(libs.androidx.junit) // AndroidX Test JUnit
    androidTestImplementation(libs.androidx.espresso.core) // Espresso Core
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose UI 테스트

    // 디버그 관련 라이브러리 (UI Tooling 등)
    debugImplementation(libs.androidx.ui.tooling) // Compose UI Tooling (Layout Inspector 등)
    debugImplementation(libs.androidx.ui.test.manifest) // Test Manifest
}