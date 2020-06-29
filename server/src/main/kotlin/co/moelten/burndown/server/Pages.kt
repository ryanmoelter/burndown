package co.moelten.burndown.server

import kotlinx.css.*
import kotlinx.html.*
import java.time.format.DateTimeFormatter

internal fun HTML.unauthenticatedIndex() {
  standardHeader()
  body {
    header {
      div {
        h1("title content") { +"Burndown" }
      }
    }
    div("content") {
      p {
        a(href = "/login") { +"Login with Fitbit" }
      }
    }
  }
}

internal fun HTML.authenticatedIndex(weights: List<WeightMeasurement>) {
  standardHeader()
  body {
    header {
      div {
        h1("title content") { +"Burndown" }
      }
    }
    div("content") {
      table {
        thead {
          tr {
            th(ThScope.col) { +"Date" }
            th(ThScope.col, "number") { +"Fat weight (lbs)" }
            th(ThScope.col, "number") { +"Fat %" }
            th(ThScope.col, "number") { +"Overall weight (lbs)" }
          }
        }
        tbody {
          weights
            .sortedByDescending { it.date }
            .forEach { weightMeasurement ->
              tr {
                td { +weightMeasurement.date.format(DateTimeFormatter.ofPattern("MMM d")) }
                td("number") { +String.format("%.2f", weightMeasurement.fat / 100 * weightMeasurement.weight) }
                td("number") { +"${String.format("%.2f", weightMeasurement.fat)}%" }
                td("number") { +String.format("%.1f", weightMeasurement.weight) }
              }
            }
        }
      }
    }
  }
}

internal fun CSSBuilder.standardCss(html: TagSelector, body: TagSelector) {
  val accentColor = Color("rgb(160, 0, 115)")

  html {
    fontFamily = "proxima-nova, sans-serif"
    fontWeight = FontWeight.w400
    fontStyle = FontStyle.normal
  }

  body {
    margin(0.px)
  }

  header {
    backgroundColor = accentColor
    color = Color("#FFFFFF")
    overflow = Overflow.auto
    marginBottom = 16.px
  }

  rule(".content") {
    maxWidth = 800.px
    paddingLeft = 24.px
    paddingRight = 24.px
    marginRight = LinearDimension.auto
    marginLeft = LinearDimension.auto
  }

  rule(".title") {
    fontFamily = "lust-script, serif"
  }

  table {
    borderSpacing = 0.px
    width = 100.pct
  }

  thead {
    backgroundColor = accentColor.withAlpha(0.1)
  }

  th {
    paddingTop = 16.px
    paddingBottom = 16.px
    paddingLeft = 8.px
    paddingRight = 8.px
  }

  td {
    paddingTop = 16.px
    paddingBottom = 16.px
    paddingLeft = 8.px
    paddingRight = 8.px
  }

  rule(".number") {
    textAlign = TextAlign.right
  }

  rule("tr:nth-child(even)") {
    backgroundColor = accentColor.withAlpha(0.1)
  }
}

private fun HTML.standardHeader() {
  head {
    title { +"Burndown" }
    link(rel = "stylesheet", href = "/styles.css", type = "text/css")
    link(rel = "stylesheet", href = "https://use.typekit.net/jie0wdu.css")
    meta("viewport", "width=device-width, initial-scale=1.0")
  }
}
