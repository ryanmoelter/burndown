plugins {
  application
  kotlin("jvm")
  kotlin("plugin.serialization") version "1.3.72"
}

group = "co.moelten.burndown"

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
  val ktorVersion = "1.3.2"
  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-netty:$ktorVersion")
  implementation("io.ktor:ktor-html-builder:$ktorVersion")
  implementation("io.ktor:ktor-auth:$ktorVersion")
  implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
  implementation("io.ktor:ktor-locations:$ktorVersion")
  implementation("io.ktor:ktor-network-tls:$ktorVersion")
  implementation("io.ktor:ktor-network-tls-certificates:$ktorVersion")

  implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
  implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
  implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
  implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")

  implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.109-kotlin-1.3.72")
  implementation("ch.qos.logback:logback-classic:1.2.3")
  val hopliteVersion = "1.2.3"
  implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
  implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
}

task("generateJks", JavaExec::class) {
  dependsOn("classes")
  classpath = sourceSets.main.get().runtimeClasspath
  main = "co.moelten.burndown.server.CertificateGeneratorKt"
}

getTasksByName("run", false).first().dependsOn("generateJks")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
  }
}

application {
  mainClassName = "io.ktor.server.netty.EngineMain"
}
