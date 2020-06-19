plugins {
  application
  kotlin("jvm")
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
  implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.109-kotlin-1.3.72")
  implementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

application {
  mainClassName = "BurndownAppKt"
}
