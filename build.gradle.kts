val ktorVersion = "3.5.1"
val exposedVersion = "0.57.0"
val kotlinxDatetimeVersion = "0.6.1"
val logbackVersion = "1.5.12"
val hikariVersion = "5.1.0"
val postgresVersion = "42.7.3"
val junitVersion = "5.10.1"
val h2Version = "2.2.224"

plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.serialization") version "2.4.0"
    id("io.ktor.plugin") version "3.5.1"
    id("com.gradleup.shadow") version "9.1.0"
    application
}

group = "dmitrykovalev.stringlife"
version = "0.0.1"

application {
    mainClass.set("dmitrykovalev.stringlife.ApplicationKt")
}

ktor {
    openApi {
        this.enabled.set(true)
        this.codeInferenceEnabled.set(true)
        this.onlyCommented.set(false)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    implementation("io.ktor:ktor-server-cors:${ktorVersion}")

    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-routing-openapi:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2:$h2Version")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
}

tasks.named<JavaExec>("run") {
    val envFile = file(".env")
    if (envFile.exists()) {
        envFile.readLines().filter { it.isNotBlank() && !it.startsWith("#") }.forEach { line ->
            val (key, value) = line.split("=", limit = 2)
            environment(key.trim(), value.trim())
        }
    }
}
