package co.moelten.burndown.server

import java.io.File

fun main() {
  val jksFile = File("build/temporary.jks").apply {
    parentFile.mkdirs()
  }

  if (!jksFile.exists()) {
    io.ktor.network.tls.certificates.generateCertificate(jksFile) // Generates the certificate
  }
}
