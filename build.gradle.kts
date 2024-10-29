plugins {
    kotlin("jvm") version "1.6.21"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.burialwithus"
version = "0.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://nexus.botwithus.net/repository/maven-snapshots/")
    }
}

dependencies {
    implementation("net.botwithus.rs3:botwithus-api:1.0.0-20241002.231022-1")
    implementation("net.botwithus.xapi.public:api:1.0.0-SNAPSHOT")
}

application {
    mainClass.set("com.burialwithus.BurialWithUs")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.burialwithus.BurialWithUs"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17" // Use the highest supported JVM target in Kotlin
    }
}
