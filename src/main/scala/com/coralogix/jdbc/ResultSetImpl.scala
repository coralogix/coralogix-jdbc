package com.coralogix.jdbc

import cats.syntax.either._
import cats.syntax.option._
import com.coralogix.sql.grpc.external.v1.SqlQueryService.QueryResponse
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

import java.sql.{
  Date,
  ResultSet,
  ResultSetMetaData,
  SQLException,
  SQLWarning,
  Statement,
  Time,
  Timestamp
}

case class ResultSetImpl(r: QueryResponse, cursorName: String, statement: StatementImpl)
    extends ResultSet with UnsupportedResultSetMethods {

  var closed = false
  var currentIndex = -1
  var lastWasNull = false
  // the first column is 1 so it matches with other methods in this class
  val columnIndices = r.columnDescriptors
    .map(_.label)
    .zipWithIndex
    .toMap
    .view
    .mapValues(_ + 1)
    .toMap
    .withDefault(label => throw new SQLException(s"No column with label: $label"))

  type SqlValue[A] = Either[String, Option[A]]

  implicit class SqlValueOps[A](v: SqlValue[A]) {
    def getOrNull(implicit ev: Null <:< A): A =
      v.fold[A](msg => throw new SQLException(msg), _.orNull)
    def getOrDefault(default: A): A =
      v.fold(msg => throw new SQLException(msg), _.getOrElse(default))
  }

  private def getValueByLabel[A](
    columnLabel: String,
    typeName: String,
    f: PartialFunction[Kind, A]
  ): SqlValue[A] =
    getValueByIndex(
      columnIndices(columnLabel),
      typeName,
      f
    )

  private def getValueByIndex[A](
    i: Int,
    typeName: String,
    f: PartialFunction[Kind, A]
  ): SqlValue[A] = {
    lastWasNull = false
    r.rows(currentIndex).values(i - 1).kind match {
      case NullValue(_)          => lastWasNull = true; None.asRight
      case k if f.isDefinedAt(k) => f(k).some.asRight
      case k                     => s"Expected $typeName but got ${k.getClass.getName} at index $i".asLeft
    }
  }

  override def next(): Boolean =
    if (currentIndex == r.rows.size - 1)
      false
    else {
      currentIndex = currentIndex + 1
      true
    }

  override def close(): Unit =
    closed = true

  override def wasNull(): Boolean = lastWasNull

  override def getString(columnIndex: Int): String =
    getValueByIndex(columnIndex, "String", { case StringValue(s) => s }).getOrNull

  override def getBoolean(columnIndex: Int): Boolean =
    getValueByIndex(columnIndex, "Boolean", { case BoolValue(s) => s }).getOrDefault(false)

  override def getByte(columnIndex: Int): Byte =
    getValueByIndex(columnIndex, "Byte", { case NumberValue(s) => s.toByte }).getOrDefault(0)

  override def getShort(columnIndex: Int): Short =
    getValueByIndex(columnIndex, "Short", { case NumberValue(s) => s.toShort }).getOrDefault(0)

  override def getInt(columnIndex: Int): Int =
    getValueByIndex(columnIndex, "Int", { case NumberValue(s) => s.toInt }).getOrDefault(0)

  override def getLong(columnIndex: Int): Long =
    getValueByIndex(columnIndex, "Long", { case NumberValue(s) => s.toLong }).getOrDefault(0)

  override def getFloat(columnIndex: Int): Float =
    getValueByIndex(columnIndex, "Float", { case NumberValue(s) => s.toFloat }).getOrDefault(0)

  override def getDouble(columnIndex: Int): Double =
    getValueByIndex(columnIndex, "Double", { case NumberValue(s) => s }).getOrDefault(0)

  override def getBigDecimal(columnIndex: Int, scale: Int): java.math.BigDecimal =
    getValueByIndex(
      columnIndex,
      "BigDecimal",
      { case NumberValue(n) => new java.math.BigDecimal(n).setScale(scale) }
    ).getOrNull

  override def getDate(columnIndex: Int): Date =
    getValueByIndex(columnIndex, "Date", { case NumberValue(s) => new Date(s.toLong) }).getOrNull

  override def getTime(columnIndex: Int): Time =
    getValueByIndex(columnIndex, "Time", { case NumberValue(s) => new Time(s.toLong) }).getOrNull

  override def getTimestamp(columnIndex: Int): Timestamp =
    getValueByIndex(
      columnIndex,
      "Timestamp",
      { case NumberValue(s) => new Timestamp(s.toLong) }
    ).getOrNull

  override def getString(columnLabel: String): String =
    getValueByLabel(columnLabel, "String", { case StringValue(s) => s }).getOrNull

  override def getBoolean(columnLabel: String): Boolean =
    getValueByLabel(columnLabel, "Boolean", { case BoolValue(s) => s }).getOrDefault(false)

  override def getByte(columnLabel: String): Byte =
    getValueByLabel(columnLabel, "Byte", { case NumberValue(s) => s.toByte }).getOrDefault(0)

  override def getShort(columnLabel: String): Short =
    getValueByLabel(columnLabel, "Short", { case NumberValue(s) => s.toShort }).getOrDefault(0)

  override def getInt(columnLabel: String): Int =
    getValueByLabel(columnLabel, "Int", { case NumberValue(s) => s.toInt }).getOrDefault(0)

  override def getLong(columnLabel: String): Long =
    getValueByLabel(columnLabel, "Long", { case NumberValue(s) => s.toLong }).getOrDefault(0)

  override def getFloat(columnLabel: String): Float =
    getValueByLabel(columnLabel, "Float", { case NumberValue(s) => s.toFloat }).getOrDefault(0)

  override def getDouble(columnLabel: String): Double =
    getValueByLabel(columnLabel, "Double", { case NumberValue(s) => s }).getOrDefault(0)

  override def getBigDecimal(columnLabel: String, scale: Int): java.math.BigDecimal =
    getValueByLabel(
      columnLabel,
      "BigDecimal",
      { case NumberValue(n) => new java.math.BigDecimal(n).setScale(scale) }
    ).getOrNull

  override def getDate(columnLabel: String): Date =
    getValueByLabel(columnLabel, "Date", { case NumberValue(s) => new Date(s.toLong) }).getOrNull

  override def getTime(columnLabel: String): Time =
    getValueByLabel(columnLabel, "Time", { case NumberValue(n) => new Time(n.toLong) }).getOrNull

  override def getTimestamp(columnLabel: String): Timestamp =
    getValueByLabel(
      columnLabel,
      "Timestamp",
      { case NumberValue(s) => new Timestamp(s.toLong) }
    ).getOrNull

  override def getWarnings: SQLWarning = null

  override def clearWarnings(): Unit = ()

  override def getCursorName: String = cursorName

  override def getMetaData: ResultSetMetaData =
    new ResultSetMetaDataImpl(this)

  override def getObject(columnIndex: Int): AnyRef =
    getValueByIndex(
      columnIndex,
      "UNKNOWN",
      {
        case BoolValue(s)   => java.lang.Boolean.valueOf(s)
        case Empty          => null
        case ListValue(s)   => s
        case NullValue(_)   => null
        case NumberValue(s) => java.lang.Double.valueOf(s)
        case StringValue(s) => s
        case StructValue(s) => s
      }
    ).getOrNull

  override def getObject(columnLabel: String): AnyRef =
    getObject(columnIndices(columnLabel))

  override def findColumn(columnLabel: String): Int =
    r.columnDescriptors.zipWithIndex.find(_._1.name == columnLabel).map(_._2 + 1).get

  override def getBigDecimal(columnIndex: Int): java.math.BigDecimal =
    getValueByIndex(
      columnIndex,
      "BigDecimal",
      { case NumberValue(n) => new java.math.BigDecimal(n) }
    ).getOrNull

  override def getBigDecimal(columnLabel: String): java.math.BigDecimal =
    getValueByLabel(
      columnLabel,
      "BigDecimal",
      { case NumberValue(n) => new java.math.BigDecimal(n) }
    ).getOrNull

  override def isBeforeFirst: Boolean =
    currentIndex == -1

  override def isAfterLast: Boolean =
    currentIndex >= r.rows.size

  override def isFirst: Boolean =
    currentIndex == 0

  override def isLast: Boolean =
    currentIndex == r.rows.size - 1

  override def getRow: Int = currentIndex + 1

  override def setFetchDirection(direction: Int): Unit =
    if (direction != ResultSet.FETCH_FORWARD)
      unsupported

  override def getFetchDirection: Int = ResultSet.FETCH_FORWARD

  override def setFetchSize(rows: Int): Unit = ()

  override def getFetchSize: Int = 0

  override def getType: Int = ResultSet.TYPE_FORWARD_ONLY

  override def getConcurrency: Int = ResultSet.CONCUR_READ_ONLY

  override def rowUpdated(): Boolean = false

  override def rowInserted(): Boolean = false

  override def rowDeleted(): Boolean = false

  override def getStatement: Statement = statement

  override def getHoldability: Int = ResultSet.HOLD_CURSORS_OVER_COMMIT

  override def isClosed: Boolean = closed

  override def getNString(columnIndex: Int): String = getString(columnIndex)

  override def getNString(columnLabel: String): String = getString(columnLabel)

  override def getObject[T](columnIndex: Int, `type`: Class[T]): T =
    getObject(columnIndex).asInstanceOf[T]

  override def getObject[T](columnLabel: String, `type`: Class[T]): T =
    getObject(columnLabel).asInstanceOf[T]

  override def isWrapperFor(iface: Class[_]): Boolean = false
}
