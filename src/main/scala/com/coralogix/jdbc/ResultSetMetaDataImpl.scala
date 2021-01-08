package com.coralogix.jdbc

import java.sql.{ ResultSetMetaData, SQLFeatureNotSupportedException }

class ResultSetMetaDataImpl(resultSet: ResultSetImpl) extends ResultSetMetaData {

  def columnDescriptor(column: Int) = resultSet.r.columnDescriptors(column - 1)

  override def getColumnCount: Int =
    resultSet.r.columnDescriptors.size

  override def isAutoIncrement(column: Int): Boolean = false

  override def isCaseSensitive(column: Int): Boolean = true

  override def isSearchable(column: Int): Boolean = true

  override def isCurrency(column: Int): Boolean = false

  override def isNullable(column: Int): Int =
    ResultSetMetaData.columnNullableUnknown

  override def isSigned(column: Int): Boolean = true

  override def getColumnDisplaySize(column: Int): Int = 10

  override def getColumnLabel(column: Int): String = columnDescriptor(column).label

  override def getColumnName(column: Int): String = columnDescriptor(column).name

  override def getSchemaName(column: Int): String = "coralogix"

  override def getPrecision(column: Int): Int = 10

  override def getScale(column: Int): Int = 10

  override def getTableName(column: Int): String = "logs"

  override def getCatalogName(column: Int): String = "coralogix"

  override def getColumnType(column: Int): Int =
    ElasticsearchType.byName(columnDescriptor(column).`type`.name).jdbcType.getVendorTypeNumber

  override def getColumnTypeName(column: Int): String =
    ElasticsearchType.byName(columnDescriptor(column).`type`.name).jdbcType.getName

  override def isReadOnly(column: Int): Boolean = true

  override def isWritable(column: Int): Boolean = false

  override def isDefinitelyWritable(column: Int): Boolean = false

  override def getColumnClassName(column: Int): String = classOf[String].getName

  override def unwrap[T](iface: Class[T]): T =
    throw new SQLFeatureNotSupportedException(s"ResultSetMetaDataImpl.unwrap")

  override def isWrapperFor(iface: Class[_]): Boolean = false
}
