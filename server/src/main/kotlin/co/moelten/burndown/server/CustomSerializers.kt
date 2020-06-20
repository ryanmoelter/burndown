package co.moelten.burndown.server

import kotlinx.serialization.*
import java.time.LocalDate

@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
  override val descriptor: SerialDescriptor
    get() = PrimitiveDescriptor("java.time.LocalDate", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): LocalDate {
    return LocalDate.parse(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: LocalDate) {
    encoder.encodeString(value.toString())
  }
}
