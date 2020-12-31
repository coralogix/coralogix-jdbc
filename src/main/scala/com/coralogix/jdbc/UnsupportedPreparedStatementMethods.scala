package com.coralogix.jdbc

import java.io.{ InputStream, Reader }
import java.net.URL
import java.sql.{ Blob, Clob, NClob, Ref, RowId, SQLXML, Time }
import java.util.Calendar

trait UnsupportedPreparedStatementMethods extends UnsupportedMethods {
  self: PreparedStatementImpl =>

  override def executeUpdate(): Int = unsupported

  override def setBytes(parameterIndex: Int, x: Array[Byte]): Unit = unsupported

  override def setTime(parameterIndex: Int, x: Time): Unit = unsupported

  override def setAsciiStream(parameterIndex: Int, x: InputStream, length: Int): Unit = unsupported

  override def setUnicodeStream(parameterIndex: Int, x: InputStream, length: Int): Unit =
    unsupported

  override def setBinaryStream(parameterIndex: Int, x: InputStream, length: Int): Unit = unsupported

  override def addBatch(): Unit = unsupported

  override def setCharacterStream(parameterIndex: Int, reader: Reader, length: Int): Unit =
    unsupported

  override def setRef(parameterIndex: Int, x: Ref): Unit = unsupported

  override def setBlob(parameterIndex: Int, x: Blob): Unit = unsupported

  override def setClob(parameterIndex: Int, x: Clob): Unit = unsupported

  override def setArray(parameterIndex: Int, x: java.sql.Array): Unit = unsupported

  override def setTime(parameterIndex: Int, x: Time, cal: Calendar): Unit = unsupported

  override def setURL(parameterIndex: Int, x: URL): Unit = unsupported

  override def setRowId(parameterIndex: Int, x: RowId): Unit = unsupported

  override def setNString(parameterIndex: Int, value: String): Unit = unsupported

  override def setNCharacterStream(parameterIndex: Int, value: Reader, length: Long): Unit =
    unsupported

  override def setNClob(parameterIndex: Int, value: NClob): Unit = unsupported

  override def setClob(parameterIndex: Int, reader: Reader, length: Long): Unit = unsupported

  override def setBlob(parameterIndex: Int, inputStream: InputStream, length: Long): Unit =
    unsupported

  override def setNClob(parameterIndex: Int, reader: Reader, length: Long): Unit = unsupported

  override def setSQLXML(parameterIndex: Int, xmlObject: SQLXML): Unit = unsupported

  override def setAsciiStream(parameterIndex: Int, x: InputStream, length: Long): Unit = unsupported

  override def setBinaryStream(parameterIndex: Int, x: InputStream, length: Long): Unit =
    unsupported

  override def setCharacterStream(parameterIndex: Int, reader: Reader, length: Long): Unit =
    unsupported

  override def setAsciiStream(parameterIndex: Int, x: InputStream): Unit = unsupported

  override def setBinaryStream(parameterIndex: Int, x: InputStream): Unit = unsupported

  override def setCharacterStream(parameterIndex: Int, reader: Reader): Unit = unsupported

  override def setNCharacterStream(i: Int, reader: Reader): Unit = unsupported

  override def setClob(parameterIndex: Int, reader: Reader): Unit = unsupported

  override def setBlob(parameterIndex: Int, inputStream: InputStream): Unit = unsupported

  override def setNClob(parameterIndex: Int, reader: Reader): Unit = unsupported
}
