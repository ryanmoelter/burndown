package co.moelten.burndown.server

import com.sksamuel.hoplite.ConfigLoader
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.OAuthServerSettings.OAuth2ServerSettings
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.origin
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.hex
import kotlinx.css.*
import kotlinx.html.*
import java.io.File

fun main() {
  embeddedServer(
    factory = Netty,
    port = 8080,
    watchPaths = listOf("server"),
    module = Application::module
  ).start()
}

data class MySession(
  val userId: String,
  val accessToken: String,
  val refreshToken: String
)

fun Application.module() {
  val config = ConfigLoader().loadConfigOrThrow<Config>(File("./config.yaml"))

  val fitbitLoginProvider = OAuth2ServerSettings(
    "fitbit",
    "https://www.fitbit.com/oauth2/authorize",
    "https://api.fitbit.com/oauth2/token",
    clientId = config.fitbitClientId,
    clientSecret = config.fitbitClientSecret,
    defaultScopes = listOf("profile", "weight"),
    requestMethod = HttpMethod.Post,
    accessTokenRequiresBasicAuth = true
  )

  install(DefaultHeaders)
  install(CallLogging)
  install(Sessions) {
    cookie<MySession>("oauthSession") {
      @OptIn(KtorExperimentalAPI::class)
      val secretSignKey = hex("c249a0d443e74742d1e3f0ff85f2d285")
      transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
    }
  }
  authentication {
    oauth("fitbit-oauth") {
      client = HttpClient(OkHttp) {
        install(Logging) {
          level = LogLevel.ALL
        }
      }
      providerLookup = { fitbitLoginProvider }
      urlProvider = { redirectUrl("/login") }
    }
  }
  routing {
    get("/") {
      val session = call.sessions.get<MySession>()
      call.respondHtml {
        standardHeader()
        body {
          h1("title") { +"Burndown" }
          p { +"Hi ${session?.userId}"}
          p {
            a(href = "/login") { +"Login with Fitbit" }
          }
        }
      }
    }

    authenticate("fitbit-oauth") {
      route("/login") {
        handle {
          val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
            ?: error("No principal")

          call.sessions.set(MySession(principal.extraParameters["user_id"]!!, principal.accessToken, principal.refreshToken!!))
          call.respondRedirect("/")
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

private fun ApplicationCall.redirectUrl(path: String): String {
  val defaultPort = if (request.origin.scheme == "http") 80 else 443
  val hostPort = request.host() + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
  val protocol = request.origin.scheme
  return "$protocol://$hostPort$path"
}

private fun HTML.standardHeader() {
  head {
    title { +"Burndown" }
    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
    link(rel = "stylesheet", href = "https://use.typekit.net/jie0wdu.css")
  }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
  this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
