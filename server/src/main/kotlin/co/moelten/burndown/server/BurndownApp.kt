package co.moelten.burndown.server

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title

fun Application.module() {
  install(DefaultHeaders)
  install(CallLogging)
  routing {
    get("/") {
      call.respondHtml {
        head {
          title { +"Burndown" }
        }
        body {
          h1 { +"Burndown" }
        }
      }
    }
  }
}

fun main() {
  embeddedServer(Netty, 8080, watchPaths = listOf("BurndownAppKt"), module = Application::module).start()
}
