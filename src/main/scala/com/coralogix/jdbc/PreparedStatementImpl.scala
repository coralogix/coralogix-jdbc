package com.coralogix.jdbc

import com.coralogix.sql.grpc.external.v1.SqlQueryService.ZioSqlQueryService.SqlQueryServiceClient
import com.coralogix.sql.grpc.external.v1.SqlQueryService.{ QueryParameter, Type }
import com.google.protobuf.struct.{ NullValue, Value }
import zio.Runtime

import java.sql.{
  Date,
  ParameterMetaData,
  PreparedStatement,
  ResultSet,
  ResultSetMetaData,
  SQLNonTransientException,
  Timestamp
}
import java.util.Calendar

class PreparedStatementImpl(
  rt: Runtime[SqlQueryServiceClient],
  connection: ConnectionImpl,
  queryTimeout: Int,
  sql: String
) extends StatementImpl(rt, connection, queryTimeout) with PreparedStatement
    with UnsupportedPreparedStatementMethods {

  //FIXME I removed opendistro driver dependency as it was not much needed, is this ok?
//  val parameters = new Array[QueryParameter](SqlParser.countParameterMarkers(sql))
  val parameters = new Array[QueryParameter](sql.count(_ == '?'))

  override def executeQuery(): ResultSet =
    callGrpc(sql, parameters.toIndexedSeq)

  private def setParameter(index: Int, `type`: Type, value: Value): Unit =
    parameters(index - 1) = QueryParameter(Some(value), `type`)

  override def setNull(parameterIndex: Int, sqlType: Int): Unit =
    setParameter(parameterIndex, Type.NULL, Proto.`null`)

  override def setBoolean(parameterIndex: Int, x: Boolean): Unit =
    setParameter(parameterIndex, Type.BOOLEAN, Value().withBoolValue(x))

  override def setByte(parameterIndex: Int, x: Byte): Unit =
    setParameter(parameterIndex, Type.BYTE, Value().withNumberValue(x.toDouble))

  override def setShort(parameterIndex: Int, x: Short): Unit =
    setParameter(parameterIndex, Type.SHORT, Value().withNumberValue(x.toDouble))

  override def setInt(parameterIndex: Int, x: Int): Unit =
    setParameter(parameterIndex, Type.INTEGER, Value().withNumberValue(x.toDouble))

  override def setLong(parameterIndex: Int, x: Long): Unit =
    setParameter(parameterIndex, Type.LONG, Value().withNumberValue(x.toDouble))

  override def setFloat(parameterIndex: Int, x: Float): Unit =
    setParameter(parameterIndex, Type.FLOAT, Value().withNumberValue(x.toDouble))

  override def setDouble(parameterIndex: Int, x: Double): Unit =
    setParameter(parameterIndex, Type.DOUBLE, Value().withNumberValue(x))

  override def setBigDecimal(parameterIndex: Int, x: java.math.BigDecimal): Unit =
    setParameter(parameterIndex, Type.DOUBLE, Value().withNumberValue(x.doubleValue()))

  override def setString(parameterIndex: Int, x: String): Unit =
    setParameter(parameterIndex, Type.STRING, Value().withStringValue(x))

  //FIXME is it ok to set all Dates and Timestamps as Longs?
  override def setDate(parameterIndex: Int, x: Date): Unit =
    setParameter(
      parameterIndex,
      Type.DATE,
      Value().withNumberValue(x.getTime.toDouble)
    )

  override def setTimestamp(parameterIndex: Int, x: Timestamp): Unit =
    setParameter(
      parameterIndex,
      Type.DATE,
      Value().withNumberValue(x.getTime.toDouble)
    )

  override def clearParameters(): Unit =
    parameters.indices.foreach(i => parameters(i) = null)

  override def setObject(parameterIndex: Int, x: Any, targetSqlType: Int): Unit =
    setObject(parameterIndex, x)

  override def setObject(parameterIndex: Int, x: Any): Unit =
    x match {
      case v: String             => setString(parameterIndex, v)
      case v: Boolean            => setBoolean(parameterIndex, v)
      case v: Byte               => setByte(parameterIndex, v)
      case v: Short              => setShort(parameterIndex, v)
      case v: Int                => setInt(parameterIndex, v)
      case v: Long               => setLong(parameterIndex, v)
      case v: Float              => setFloat(parameterIndex, v)
      case v: Double             => setDouble(parameterIndex, v)
      case v: Array[Byte]        => setBytes(parameterIndex, v)
      case v: java.sql.Date      => setDate(parameterIndex, v)
      case v: java.sql.Timestamp => setTimestamp(parameterIndex, v)
      case _ =>
        throw new SQLNonTransientException(
          s"PreparedStatementImpl.setObject: value ${x.toString} of non supported type ${x.getClass.getName}"
        )
    }

  override def execute(): Boolean = {
    callGrpc(sql, parameters.toIndexedSeq)
    true
  }

  override def getMetaData: ResultSetMetaData = null

  override def setDate(parameterIndex: Int, x: Date, cal: Calendar): Unit =
    setParameter(parameterIndex, Type.DATE, Value().withNumberValue(x.getTime.toDouble))

  override def setTimestamp(parameterIndex: Int, x: Timestamp, calendar: Calendar): Unit =
    setParameter(parameterIndex, Type.DATE, Value().withNumberValue(x.getTime.toDouble))

  override def setNull(parameterIndex: Int, sqlType: Int, typeName: String): Unit =
    setNull(parameterIndex, sqlType)

  override def getParameterMetaData: ParameterMetaData = null

  override def setObject(
    parameterIndex: Int,
    x: Any,
    targetSqlType: Int,
    scaleOrLength: Int
  ): Unit =
    setObject(parameterIndex, x)
}
