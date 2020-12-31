package com.coralogix.jdbc

import java.sql.{ JDBCType, Timestamp }

case class ElasticsearchType(
  name: String,
  jdbcType: JDBCType,
  javaClass: Class[_],
  precision: Int,
  displaySize: Int,
  isSigned: Boolean
)

//FIXME almost copy from opendistro
object ElasticsearchType {

  // Precision values based on number of decimal digits supported by Java types
  // Display size values based on precision plus additional buffer for visual representation
  // - Java long is a 64-bit integral value ~ 19 decimal digits
  // - Java double has 53-bit precision ~ 15 decimal digits
  // - Java float has 24-bit precision ~ 7 decimal digits
  // - scaled_float is internally an elasticsearch long, but treated as Java Double here
  // - ISO8601 representation of DateTime values as yyyy-mm-ddThh:mm:ss.mmmZ ~ 24 chars

  // Some Types not fully supported yet: VARBINARY, GEO_POINT, NESTED
  val BOOLEAN = ElasticsearchType("BOOLEAN", JDBCType.BOOLEAN, classOf[Boolean], 1, 1, false)
  val BYTE = ElasticsearchType("BYTE", JDBCType.TINYINT, classOf[Byte], 3, 5, true)
  val SHORT = ElasticsearchType("SHORT", JDBCType.SMALLINT, classOf[Short], 5, 6, true)
  val INTEGER = ElasticsearchType("INTEGER", JDBCType.INTEGER, classOf[Integer], 10, 11, true)
  val LONG = ElasticsearchType("LONG", JDBCType.BIGINT, classOf[Long], 19, 20, true)
  val HALF_FLOAT = ElasticsearchType("HALF_FLOAT", JDBCType.REAL, classOf[Float], 7, 15, true)
  val FLOAT = ElasticsearchType("FLOAT", JDBCType.REAL, classOf[Float], 7, 15, true)
  val DOUBLE = ElasticsearchType("DOUBLE", JDBCType.DOUBLE, classOf[Double], 15, 25, true)
  val SCALED_FLOAT =
    ElasticsearchType("SCALED_FLOAT", JDBCType.DOUBLE, classOf[Double], 15, 25, true)
  val KEYWORD = ElasticsearchType("KEYWORD", JDBCType.VARCHAR, classOf[String], 256, 0, false)
  val TEXT =
    ElasticsearchType("TEXT", JDBCType.VARCHAR, classOf[String], Integer.MAX_VALUE, 0, false)
  val STRING =
    ElasticsearchType("STRING", JDBCType.VARCHAR, classOf[String], Integer.MAX_VALUE, 0, false)
  val IP = ElasticsearchType("IP", JDBCType.VARCHAR, classOf[String], 15, 0, false)
  val NESTED = ElasticsearchType("NESTED", JDBCType.STRUCT, null, 0, 0, false)
  val OBJECT = ElasticsearchType("OBJECT", JDBCType.STRUCT, null, 0, 0, false)
  val DATE = ElasticsearchType("DATE", JDBCType.TIMESTAMP, classOf[Timestamp], 24, 24, false)
  val NULL = ElasticsearchType("NULL", JDBCType.NULL, null, 0, 0, false)
  val UNSUPPORTED = ElasticsearchType("UNSUPPORTED", JDBCType.OTHER, null, 0, 0, false)

  val all = List(
    BOOLEAN,
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    HALF_FLOAT,
    FLOAT,
    DOUBLE,
    SCALED_FLOAT,
    KEYWORD,
    TEXT,
    STRING,
    IP,
    NESTED,
    OBJECT,
    DATE,
    NULL,
    UNSUPPORTED
  )

  val byName: Map[String, ElasticsearchType] =
    all.map(t => (t.name, t)).toMap.withDefaultValue(STRING)
}
