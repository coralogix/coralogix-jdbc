package com.coralogix.jdbc

import com.coralogix.sql.grpc.external.v1.SqlQueryService.ZioSqlQueryService.SqlQueryServiceClient

import java.sql.{
  Connection,
  DatabaseMetaData,
  PreparedStatement,
  ResultSet,
  SQLFeatureNotSupportedException,
  SQLNonTransientException,
  SQLWarning,
  Statement
}
import java.util.concurrent.Executor
import zio._

class ConnectionImpl(rt: Runtime[SqlQueryServiceClient], val url: String, queryTimeout: Int)
    extends java.sql.Connection with UnsupportedConnectionMethods {

  private var open = true

  override def createStatement(): Statement =
    new StatementImpl(rt, this, queryTimeout)

  override def prepareStatement(sql: String): PreparedStatement =
    new PreparedStatementImpl(rt, this, queryTimeout, sql)

  override def setAutoCommit(autoCommit: Boolean): Unit = ()

  override def getAutoCommit: Boolean = true

  override def commit(): Unit = ()

  override def rollback(): Unit = ()

  override def close(): Unit = open = false

  override def isClosed: Boolean = !open

  override def getMetaData: DatabaseMetaData = new DatabaseMetaDataImpl(this)

  override def setReadOnly(readOnly: Boolean): Unit =
    if (!readOnly) throw new SQLNonTransientException("Read-write connection not supported")

  override def isReadOnly: Boolean = true

  override def getTransactionIsolation: Int =
    Connection.TRANSACTION_NONE

  override def getWarnings: SQLWarning = null

  override def clearWarnings(): Unit = ()

  override def createStatement(resultSetType: Int, resultSetConcurrency: Int): Statement =
    createStatement(resultSetType, resultSetConcurrency, ResultSet.HOLD_CURSORS_OVER_COMMIT)

  override def prepareStatement(
    sql: String,
    resultSetType: Int,
    resultSetConcurrency: Int
  ): PreparedStatement =
    prepareStatement(sql, resultSetType, resultSetConcurrency, ResultSet.HOLD_CURSORS_OVER_COMMIT)

  private def validateResultSetCharacteristics(
    resultSetType: Int,
    resultSetConcurrency: Int,
    holdability: Int
  ): Unit = {
    if (resultSetType != ResultSet.TYPE_FORWARD_ONLY)
      throw new SQLNonTransientException(
        s"Unsupported resultSetType `$resultSetType`. Only TYPE_FORWARD_ONLY is supported."
      )
    if (resultSetConcurrency != ResultSet.CONCUR_READ_ONLY)
      throw new SQLNonTransientException(
        s"Unsupported resultSetConcurrency `$resultSetConcurrency`. Only CONCUR_READ_ONLY is supported."
      )
    if (holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT)
      throw new SQLNonTransientException(
        s"Unsupported holdability `$holdability`. Only HOLD_CURSORS_OVER_COMMIT is supported."
      )
  }

  override def createStatement(
    resultSetType: Int,
    resultSetConcurrency: Int,
    holdability: Int
  ): Statement = {
    validateResultSetCharacteristics(resultSetType, resultSetConcurrency, holdability)
    createStatement()
  }

  override def prepareStatement(
    sql: String,
    resultSetType: Int,
    resultSetConcurrency: Int,
    holdability: Int
  ): PreparedStatement = {
    validateResultSetCharacteristics(resultSetType, resultSetConcurrency, holdability)
    prepareStatement(sql)
  }

  override def prepareStatement(sql: String, autoGeneratedKeys: Int): PreparedStatement =
    if (autoGeneratedKeys != Statement.NO_GENERATED_KEYS)
      throw new SQLFeatureNotSupportedException("Auto generated keys are not supported.")
    else
      prepareStatement(sql)

  override def abort(executor: Executor): Unit = close()

  override def setNetworkTimeout(executor: Executor, milliseconds: Int): Unit = ()

  override def getNetworkTimeout: Int = 0

  override def isWrapperFor(iface: Class[_]): Boolean = false
}
