// Top-level build file where you can add configuration options common to all sub-projects/modules.
fun implementation(string: String) {}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    implementation("androidx.navigation:navigation-compose:2.8.9")
}