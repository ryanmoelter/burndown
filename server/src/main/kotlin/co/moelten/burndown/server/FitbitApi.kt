package co.moelten.burndown.server

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import java.time.LocalDate

fun HttpClient.authorizeToFitbit(accessToken: String) = FitbitApi(this, accessToken)

private val baseUrl = "https://api.fitbit.com/1/user/-/"

class FitbitApi(
  private val client: HttpClient,
  private val accessToken: String
) {

  private suspend inline fun <reified T> getFromFitbit(urlString: String): T = client.get(urlString) {
    headers.append("Authorization", "Bearer $accessToken")
  }

  suspend fun getWeightForTheLastMonth(endDate: LocalDate): WeightResponse {
    return getFromFitbit("${baseUrl}body/log/weight/date/$endDate/1m.json")
  }
}

@Serializable
data class WeightResponse(
  val weight: List<WeightMeasurement>
)

@Serializable
data class WeightMeasurement(
  val bmi: Double,
  val weight: Double,
  val fat: Double,
  @Serializable(with = LocalDateSerializer::class) val date: LocalDate,
  val logId: Long,
  val time: String,
  val source: String
)
