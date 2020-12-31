package com.coralogix.jdbc

import java.{ sql, util }
import java.sql.{
  Blob,
  CallableStatement,
  Clob,
  NClob,
  PreparedStatement,
  SQLXML,
  Savepoint,
  Struct
}
import java.util.Properties

trait UnsupportedConnectionMethods extends UnsupportedMethods { self: ConnectionImpl =>

  override def prepareStatement(sql: String, columnIndexes: Array[Int]): PreparedStatement =
    unsupported

  override def prepareStatement(sql: String, columnNames: Array[String]): PreparedStatement =
    unsupported

  override def nativeSQL(sql: String): String = unsupported

  override def prepareCall(
    sql: String,
    resultSetType: Int,
    resultSetConcurrency: Int
  ): CallableStatement = unsupported

  override def prepareCall(
    sql: String,
    resultSetType: Int,
    resultSetConcurrency: Int,
    resultSetHoldability: Int
  ): CallableStatement = unsupported

  override def getTypeMap: util.Map[String, Class[_]] = unsupported

  override def setTypeMap(map: util.Map[String, Class[_]]): Unit = unsupported

  override def setHoldability(holdability: Int): Unit = unsupported

  override def getHoldability: Int = unsupported

  override def setSavepoint(): Savepoint = unsupported

  override def setSavepoint(name: String): Savepoint = unsupported

  override def rollback(savepoint: Savepoint): Unit = unsupported

  override def releaseSavepoint(savepoint: Savepoint): Unit = unsupported

  override def setCatalog(catalog: String): Unit = unsupported

  override def getCatalog: String = unsupported

  override def setTransactionIsolation(level: Int): Unit = unsupported

  override def prepareCall(sql: String): CallableStatement = unsupported

  override def createClob(): Clob = unsupported

  override def createBlob(): Blob = unsupported

  override def createNClob(): NClob = unsupported

  override def createSQLXML(): SQLXML = unsupported

  override def isValid(timeout: Int): Boolean = unsupported

  override def setClientInfo(name: String, value: String): Unit = unsupported

  override def setClientInfo(properties: Properties): Unit = unsupported

  override def getClientInfo(name: String): String = unsupported

  override def getClientInfo: Properties = unsupported

  override def createArrayOf(typeName: String, elements: Array[AnyRef]): sql.Array = unsupported

  override def createStruct(typeName: String, attributes: Array[AnyRef]): Struct = unsupported

  override def setSchema(schema: String): Unit = unsupported

  override def getSchema: String = unsupported

  override def unwrap[T](iface: Class[T]): T = unsupported
}
