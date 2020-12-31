package com.coralogix.jdbc

import java.io.{ InputStream, Reader }
import java.net.URL
import java.{ sql, util }
import java.sql.{ Blob, Clob, Date, NClob, Ref, RowId, SQLXML, Time, Timestamp }
import java.util.Calendar

trait UnsupportedResultSetMethods extends UnsupportedMethods { self: ResultSetImpl =>

  override def getRef(columnIndex: Int): Ref = unsupported

  override def getBlob(columnIndex: Int): Blob = unsupported

  override def getClob(columnIndex: Int): Clob = unsupported

  override def getArray(columnIndex: Int): sql.Array = unsupported

  override def getRef(columnLabel: String): Ref = unsupported

  override def getBlob(columnLabel: String): Blob = unsupported

  override def getClob(columnLabel: String): Clob = unsupported

  override def getArray(columnLabel: String): sql.Array = unsupported

  override def getURL(columnIndex: Int): URL = unsupported

  override def getURL(columnLabel: String): URL = unsupported

  override def getAsciiStream(columnLabel: String): InputStream = unsupported

  override def getUnicodeStream(columnLabel: String): InputStream = unsupported

  override def getBinaryStream(columnLabel: String): InputStream = unsupported

  override def getAsciiStream(columnIndex: Int): InputStream = unsupported

  override def getUnicodeStream(columnIndex: Int): InputStream = unsupported

  override def getBinaryStream(columnIndex: Int): InputStream = unsupported

  override def getBytes(columnLabel: String): Array[Byte] = unsupported

  override def getBytes(columnIndex: Int): Array[Byte] = unsupported

  override def getCharacterStream(columnIndex: Int): Reader = unsupported

  override def getCharacterStream(columnLabel: String): Reader = unsupported

  override def getNCharacterStream(columnIndex: Int): Reader = unsupported

  override def getNCharacterStream(columnLabel: String): Reader = unsupported

  override def getNClob(columnIndex: Int): NClob = unsupported

  override def getNClob(columnLabel: String): NClob = unsupported

  override def getSQLXML(columnIndex: Int): SQLXML = unsupported

  override def getSQLXML(columnLabel: String): SQLXML = unsupported

  override def updateNString(columnIndex: Int, nString: String): Unit = unsupported

  override def updateNString(columnLabel: String, nString: String): Unit = unsupported

  override def updateNClob(columnIndex: Int, nClob: NClob): Unit = unsupported

  override def updateNClob(columnLabel: String, nClob: NClob): Unit = unsupported

  override def updateSQLXML(columnIndex: Int, xmlObject: SQLXML): Unit = unsupported

  override def updateSQLXML(columnLabel: String, xmlObject: SQLXML): Unit = unsupported

  override def updateNull(columnIndex: Int): Unit = unsupported

  override def updateBoolean(columnIndex: Int, x: Boolean): Unit = unsupported

  override def updateByte(columnIndex: Int, x: Byte): Unit = unsupported

  override def updateShort(columnIndex: Int, x: Short): Unit = unsupported

  override def updateInt(columnIndex: Int, x: Int): Unit = unsupported

  override def updateLong(columnIndex: Int, x: Long): Unit = unsupported

  override def updateFloat(columnIndex: Int, x: Float): Unit = unsupported

  override def updateDouble(columnIndex: Int, x: Double): Unit = unsupported

  override def updateBigDecimal(columnIndex: Int, x: java.math.BigDecimal): Unit = unsupported

  override def updateString(columnIndex: Int, x: String): Unit = unsupported

  override def updateBytes(columnIndex: Int, x: Array[Byte]): Unit = unsupported

  override def updateDate(columnIndex: Int, x: Date): Unit = unsupported

  override def updateTime(columnIndex: Int, x: Time): Unit = unsupported

  override def updateTimestamp(columnIndex: Int, x: Timestamp): Unit = unsupported

  override def updateAsciiStream(columnIndex: Int, x: InputStream, length: Int): Unit = unsupported

  override def updateBinaryStream(columnIndex: Int, x: InputStream, length: Int): Unit = unsupported

  override def updateCharacterStream(columnIndex: Int, x: Reader, length: Int): Unit = unsupported

  override def updateObject(columnIndex: Int, x: Any, scaleOrLength: Int): Unit = unsupported

  override def updateObject(columnIndex: Int, x: Any): Unit = unsupported

  override def updateNull(columnLabel: String): Unit = unsupported

  override def updateBoolean(columnLabel: String, x: Boolean): Unit = unsupported

  override def updateByte(columnLabel: String, x: Byte): Unit = unsupported

  override def updateShort(columnLabel: String, x: Short): Unit = unsupported

  override def updateInt(columnLabel: String, x: Int): Unit = unsupported

  override def updateLong(columnLabel: String, x: Long): Unit = unsupported

  override def updateFloat(columnLabel: String, x: Float): Unit = unsupported

  override def updateDouble(columnLabel: String, x: Double): Unit = unsupported

  override def updateBigDecimal(columnLabel: String, x: java.math.BigDecimal): Unit = unsupported

  override def updateString(columnLabel: String, x: String): Unit = unsupported

  override def updateBytes(columnLabel: String, x: Array[Byte]): Unit = unsupported

  override def updateDate(columnLabel: String, x: Date): Unit = unsupported

  override def updateTime(columnLabel: String, x: Time): Unit = unsupported

  override def updateTimestamp(columnLabel: String, x: Timestamp): Unit = unsupported

  override def updateAsciiStream(columnLabel: String, x: InputStream, length: Int): Unit =
    unsupported

  override def updateBinaryStream(columnLabel: String, x: InputStream, length: Int): Unit =
    unsupported

  override def updateCharacterStream(columnLabel: String, x: Reader, length: Int): Unit =
    unsupported

  override def updateObject(columnLabel: String, x: Any, scaleOrLength: Int): Unit = unsupported

  override def updateObject(columnLabel: String, x: Any): Unit = unsupported

  override def insertRow(): Unit = unsupported

  override def updateRow(): Unit = unsupported

  override def deleteRow(): Unit = unsupported

  override def refreshRow(): Unit = unsupported

  override def cancelRowUpdates(): Unit = unsupported

  override def moveToInsertRow(): Unit = unsupported

  override def moveToCurrentRow(): Unit = unsupported

  override def updateRef(columnIndex: Int, x: Ref): Unit = unsupported

  override def updateRef(columnLabel: String, x: Ref): Unit = unsupported

  override def updateBlob(columnIndex: Int, x: Blob): Unit = unsupported

  override def updateBlob(columnLabel: String, x: Blob): Unit = unsupported

  override def updateClob(columnIndex: Int, x: Clob): Unit = unsupported

  override def updateClob(columnLabel: String, x: Clob): Unit = unsupported

  override def updateArray(columnIndex: Int, x: sql.Array): Unit = unsupported

  override def updateArray(columnLabel: String, x: sql.Array): Unit = unsupported

  override def updateNCharacterStream(columnIndex: Int, x: Reader, length: Long): Unit = unsupported

  override def updateNCharacterStream(columnLabel: String, reader: Reader, length: Long): Unit =
    unsupported

  override def updateAsciiStream(columnIndex: Int, x: InputStream, length: Long): Unit = unsupported

  override def updateBinaryStream(columnIndex: Int, x: InputStream, length: Long): Unit =
    unsupported

  override def updateCharacterStream(columnIndex: Int, x: Reader, length: Long): Unit = unsupported

  override def updateAsciiStream(columnLabel: String, x: InputStream, length: Long): Unit =
    unsupported

  override def updateBinaryStream(columnLabel: String, x: InputStream, length: Long): Unit =
    unsupported

  override def updateCharacterStream(columnLabel: String, reader: Reader, length: Long): Unit =
    unsupported

  override def updateBlob(columnIndex: Int, inputStream: InputStream, length: Long): Unit =
    unsupported

  override def updateBlob(columnLabel: String, inputStream: InputStream, length: Long): Unit =
    unsupported

  override def updateClob(columnIndex: Int, reader: Reader, length: Long): Unit = unsupported

  override def updateClob(columnLabel: String, reader: Reader, length: Long): Unit = unsupported

  override def updateNClob(columnIndex: Int, reader: Reader, length: Long): Unit = unsupported

  override def updateNClob(columnLabel: String, reader: Reader, length: Long): Unit = unsupported

  override def updateNCharacterStream(columnIndex: Int, x: Reader): Unit = unsupported

  override def updateNCharacterStream(columnLabel: String, reader: Reader): Unit = unsupported

  override def updateAsciiStream(columnIndex: Int, x: InputStream): Unit = unsupported

  override def updateBinaryStream(columnIndex: Int, x: InputStream): Unit = unsupported

  override def updateCharacterStream(columnIndex: Int, x: Reader): Unit = unsupported

  override def updateAsciiStream(columnLabel: String, x: InputStream): Unit = unsupported

  override def updateBinaryStream(columnLabel: String, x: InputStream): Unit = unsupported

  override def updateCharacterStream(columnLabel: String, reader: Reader): Unit = unsupported

  override def updateBlob(columnIndex: Int, inputStream: InputStream): Unit = unsupported

  override def updateBlob(columnLabel: String, inputStream: InputStream): Unit = unsupported

  override def updateClob(columnIndex: Int, reader: Reader): Unit = unsupported

  override def updateClob(columnLabel: String, reader: Reader): Unit = unsupported

  override def updateNClob(columnIndex: Int, reader: Reader): Unit = unsupported

  override def updateNClob(columnLabel: String, reader: Reader): Unit = unsupported

  override def beforeFirst(): Unit = unsupported

  override def afterLast(): Unit = unsupported

  override def first(): Boolean = unsupported

  override def last(): Boolean = unsupported

  override def getRowId(columnIndex: Int): RowId = unsupported

  override def getRowId(columnLabel: String): RowId = unsupported

  override def updateRowId(columnIndex: Int, x: RowId): Unit = unsupported

  override def updateRowId(columnLabel: String, x: RowId): Unit = unsupported

  override def absolute(row: Int): Boolean = unsupported

  override def relative(rows: Int): Boolean = unsupported

  override def previous(): Boolean = unsupported

  override def unwrap[T](iface: Class[T]): T = unsupported

  override def getObject(columnIndex: Int, map: util.Map[String, Class[_]]): AnyRef = unsupported

  override def getObject(columnLabel: String, map: util.Map[String, Class[_]]): AnyRef = unsupported

  override def getDate(columnIndex: Int, cal: Calendar): Date = unsupported

  override def getDate(columnLabel: String, cal: Calendar): Date = unsupported

  override def getTime(columnIndex: Int, cal: Calendar): Time = unsupported

  override def getTime(columnLabel: String, cal: Calendar): Time = unsupported

  override def getTimestamp(columnIndex: Int, cal: Calendar): Timestamp = unsupported

  override def getTimestamp(columnLabel: String, cal: Calendar): Timestamp = unsupported
}
