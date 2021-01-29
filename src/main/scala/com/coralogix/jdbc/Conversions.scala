package com.coralogix.jdbc

import com.google.protobuf.struct.Value.Kind
import com.google.protobuf.struct.Value.Kind.{
  BoolValue,
  Empty,
  ListValue,
  NullValue,
  NumberValue,
  StringValue,
  StructValue
}

import java.sql.Timestamp
import java.time.format.DateTimeFormatter.{ ISO_LOCAL_DATE, ISO_LOCAL_TIME }
import java.time.format.DateTimeFormatterBuilder
import java.time.{ Instant, LocalDate, LocalDateTime, OffsetDateTime, ZoneId, ZoneOffset }
import java.util.Calendar
import scala.util.Try

object Conversions {

  def anyValue(value: Kind): AnyRef =
    value match {
      case BoolValue(s)   => java.lang.Boolean.valueOf(s)
      case Empty          => null
      case ListValue(s)   => s
      case NullValue(_)   => null
      case NumberValue(s) => java.lang.Double.valueOf(s)
      case StringValue(s) => s
      case StructValue(s) => s
    }

  val stringValue: PartialFunction[Kind, String] = {
    case BoolValue(s)   => s.toString
    case Empty          => null
    case NullValue(_)   => null
    case NumberValue(s) => s.toString
    case StringValue(s) => s
  }

  private val formatter = new DateTimeFormatterBuilder().parseCaseInsensitive
    .append(ISO_LOCAL_DATE)
    .optionalStart()
    .appendLiteral('T')
    .append(ISO_LOCAL_TIME)
    .optionalStart
    .appendOffsetId
    .toFormatter

  def instantFromISO(s: String, zone: ZoneId): Option[Instant] =
    Try(
      formatter.parseBest(s, OffsetDateTime.from(_), LocalDateTime.from(_), LocalDate.from(_))
    ).toOption.map({
      case a: OffsetDateTime => a.toInstant
      case a: LocalDateTime  => a.toInstant(zone.getRules.getOffset(a))
      case a: LocalDate      => a.atStartOfDay(zone).toInstant
    })

  val timestampValue: PartialFunction[Kind, Timestamp] = Function.unlift({
    case NumberValue(s) => Some(new Timestamp(s.toLong))
    case StringValue(s) => instantFromISO(s, ZoneOffset.UTC).map(Timestamp.from)
    case _              => None
  })

  def timestampValue(cal: Calendar): PartialFunction[Kind, Timestamp] =
    Function.unlift({
      case NumberValue(s) => Some(new Timestamp(s.toLong))
      case StringValue(s) => instantFromISO(s, cal.getTimeZone.toZoneId).map(Timestamp.from)
      case _              => None
    })

}
