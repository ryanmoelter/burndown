package co.moelten.burndown.server

import io.ktor.client.HttpClient
import io.ktor.client.features.ResponseException
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import java.time.LocalDate

fun HttpClient.authorizeToFitbit(sessionData: FitbitSessionData) = FitbitApi(this, sessionData)

private const val baseFitbitUrl = "https://api.fitbit.com/"
private const val baseApiUrl = "$baseFitbitUrl/1/user/-/"

class FitbitApi(
  private val client: HttpClient,
  sessionData: FitbitSessionData
) {

  private val accessToken = sessionData.accessToken

  private suspend inline fun <reified T> getFromFitbit(urlString: String): Result<T> {
    return try {
      Result.Success(client.get(urlString) {
        headers.append("Authorization", "Bearer $accessToken")
        headers.append("Accept-Language", "en_US")
      })
    } catch (responseException: ResponseException) {
      Result.Error(responseException)
    }
  }

  suspend fun getWeightForTheLastMonth(endDate: LocalDate): Result<WeightResponse> {
    return getFromFitbit("${baseApiUrl}body/log/weight/date/$endDate/1m.json")
  }
}

sealed class Result<out Value> {
  data class Success<out Value>(val value: Value) : Result<Value>()
  data class Error<Value>(val error: ResponseException) : Result<Value>()
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
