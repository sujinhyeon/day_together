// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // [추가] 이 플러그인은 첫 번째 코드에만 있던 고유한 내용입니다.
    id("com.google.gms.google-services") version "4.4.2" apply false
}