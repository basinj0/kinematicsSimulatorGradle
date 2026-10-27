import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.4.10"
    // application
    id("org.jetbrains.compose") version "1.10.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("org.jetbrains.compose.hot-reload") version "1.0.0-rc02"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.knowm.xchart:xchart:3.8.1")
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

//application {
//    mainClass.set("MainKt")
//}

tasks.withType<org.jetbrains.compose.reload.gradle.ComposeHotRun>().configureEach {
    mainClass.set("MainKt")
}

tasks.withType<AbstractCopyTask> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Dmg, TargetFormat.Deb)
            packageName = "KinematicsSimulator"
            packageVersion = "0.3.0"

            windows {
                dirChooser = true
                menu = true
                shortcut = true
                menuGroup = "Tools"
            }
        }
    }
}
