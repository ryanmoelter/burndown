package co.moelten.burndown.server

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.css.*
import kotlinx.html.*

fun main() {
  embeddedServer(
    factory = Netty,
    port = 8080,
    watchPaths = listOf("server"),
    module = Application::module
  ).start()
}

fun Application.module() {
  install(DefaultHeaders)
  install(CallLogging)
  routing {
    get("/") {
      call.respondHtml {
        head {
          title { +"Burndown" }
          link(rel = "stylesheet", href = "/styles.css", type = "text/css")
          link(rel = "stylesheet", href = "https://use.typekit.net/jie0wdu.css")
        }
        body {
          h1("title") { +"Burndown " }
        }
      }
    }

    get("/styles.css") {
      call.respondCss {
        html {
          fontFamily = "proxima-nova, sans-serif"
          fontWeight = FontWeight.w400
          fontStyle = FontStyle.normal
        }

        body {
          maxWidth = 800.px
          marginRight = LinearDimension.auto
          marginLeft = LinearDimension.auto
        }

        rule(".title") {
          fontFamily = "lust-script, serif"
        }
      }
    }
  }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
  this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
