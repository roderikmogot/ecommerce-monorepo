plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    // --- Core Dependencies ---
    // Spring WebFlux
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Spring Data R2DBC
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    // Spring Boot's SQL Initializer
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    // Actuator for monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // Jackson module for Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // Kotlin Coroutines support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // R2DBC PostgreSQL driver
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    runtimeOnly("io.r2dbc:r2dbc-pool")

    // Prometheus for metrics
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // --- Testing Dependencies ---
    // Core testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // Test with coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    // Mockito for mocking in tests
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    // Testcontainers for integration testing with a real DB
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.mockk:mockk:1.13.5")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}
