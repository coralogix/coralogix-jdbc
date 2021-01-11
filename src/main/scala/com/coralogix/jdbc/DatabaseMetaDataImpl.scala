package com.coralogix.jdbc

import Proto._
import com.coralogix.sql.grpc.external.v1.SqlQueryService.ZioSqlQueryService.SqlQueryServiceClient
import com.coralogix.sql.grpc.external.v1.SqlQueryService.{
  ColumnDescriptor,
  QueryResponse,
  Row,
  SchemaRequest,
  Type
}
import com.google.protobuf.empty.Empty
import com.google.protobuf.struct.Value
import zio.{ Cause, Runtime, ZIO }
import zio.duration._

import java.sql.{
  Connection,
  DatabaseMetaData,
  JDBCType,
  ResultSet,
  RowIdLifetime,
  SQLException,
  SQLFeatureNotSupportedException
}

class DatabaseMetaDataImpl(
  rt: Runtime[SqlQueryServiceClient],
  queryTimeout: Int,
  connection: ConnectionImpl
) extends DatabaseMetaData {

  val className = classOf[DatabaseMetaDataImpl].getName

  def cd(column: String, `type`: Type = Type.STRING) =
    ColumnDescriptor(column, column, `type`)

  def s(column: String, value: String) =
    (column, Type.STRING, str(value))

  def s(column: String, `type`: Type = Type.STRING) =
    (column, `type`, `null`)

  def singleRowResultSet(
    ms: (String, Type, Value)*
  ) =
    ResultSetImpl(
      QueryResponse(
        List(Row(ms.map(m => m._3))),
        ms.map(m => ColumnDescriptor(m._1, m._1, m._2)).toList
      ),
      "cursor",
      null
    )

  def e(column: String, `type`: Type = Type.STRING) =
    (column, `type`)

  def emptyResultSet(
    ms: (String, Type)*
  ) =
    ResultSetImpl(
      QueryResponse(
        Nil,
        ms.map(m => ColumnDescriptor(m._1, m._1, m._2)).toList
      ),
      "cursor",
      null
    )

  def unsupported = {
    val st = Thread.currentThread().getStackTrace.findLast(_.getClassName == className).get
    val methodName = st.getMethodName
    val lineNumber = st.getLineNumber
    throw new SQLFeatureNotSupportedException(s"$className.$methodName:$lineNumber")
  }

  override def allProceduresAreCallable(): Boolean = true

  override def allTablesAreSelectable(): Boolean = true

  override def getURL: String = connection.url

  override def getUserName: String = unsupported

  override def isReadOnly: Boolean = true

  override def nullsAreSortedHigh(): Boolean = false

  override def nullsAreSortedLow(): Boolean = false

  override def nullsAreSortedAtStart(): Boolean = false

  override def nullsAreSortedAtEnd(): Boolean = true

  override def getDatabaseProductName: String = "Coralogix"

  override def getDatabaseProductVersion: String = "0"

  override def getDriverName: String = "Coralogix JDBC Driver"

  override def getDriverVersion: String = s"$getDriverMajorVersion.$getDriverMinorVersion"

  override def getDriverMajorVersion: Int = 0

  override def getDriverMinorVersion: Int = 0

  override def usesLocalFiles(): Boolean = true

  override def usesLocalFilePerTable(): Boolean = true

  override def supportsMixedCaseIdentifiers(): Boolean = true

  override def storesUpperCaseIdentifiers(): Boolean = false

  override def storesLowerCaseIdentifiers(): Boolean = false

  override def storesMixedCaseIdentifiers(): Boolean = false

  override def supportsMixedCaseQuotedIdentifiers(): Boolean = true

  override def storesUpperCaseQuotedIdentifiers(): Boolean = false

  override def storesLowerCaseQuotedIdentifiers(): Boolean = false

  override def storesMixedCaseQuotedIdentifiers(): Boolean = false

  // space to indicate quoting not supported currently
  override def getIdentifierQuoteString: String = " "

  override def getSQLKeywords: String = ""

  override def getNumericFunctions: String = ""

  override def getStringFunctions: String = ""

  override def getSystemFunctions: String = ""

  override def getTimeDateFunctions: String = ""

  override def getSearchStringEscape: String = "\\"

  override def getExtraNameCharacters: String = ""

  override def supportsAlterTableWithAddColumn(): Boolean = false

  override def supportsAlterTableWithDropColumn(): Boolean = false

  override def supportsColumnAliasing(): Boolean = true

  override def nullPlusNonNullIsNull(): Boolean = true

  override def supportsConvert(): Boolean = false

  override def supportsConvert(fromType: Int, toType: Int): Boolean = false

  override def supportsTableCorrelationNames(): Boolean = true

  override def supportsDifferentTableCorrelationNames(): Boolean = false

  override def supportsExpressionsInOrderBy(): Boolean = false

  override def supportsOrderByUnrelated(): Boolean = true

  override def supportsGroupBy(): Boolean = true

  override def supportsGroupByUnrelated(): Boolean = true

  override def supportsGroupByBeyondSelect(): Boolean = true

  override def supportsLikeEscapeClause(): Boolean = true

  override def supportsMultipleResultSets(): Boolean = false

  override def supportsMultipleTransactions(): Boolean = false

  override def supportsNonNullableColumns(): Boolean = true

  override def supportsMinimumSQLGrammar(): Boolean = true

  override def supportsCoreSQLGrammar(): Boolean = false

  override def supportsExtendedSQLGrammar(): Boolean = false

  override def supportsANSI92EntryLevelSQL(): Boolean = true

  override def supportsANSI92IntermediateSQL(): Boolean = false

  override def supportsANSI92FullSQL(): Boolean = false

  override def supportsIntegrityEnhancementFacility(): Boolean = false

  override def supportsOuterJoins(): Boolean = true

  override def supportsFullOuterJoins(): Boolean = false

  override def supportsLimitedOuterJoins(): Boolean = true

  override def getSchemaTerm: String = "schema"

  override def getProcedureTerm: String = "procedure"

  override def getCatalogTerm: String = "catalog"

  override def isCatalogAtStart: Boolean = false

  override def getCatalogSeparator: String = "."

  override def supportsSchemasInDataManipulation(): Boolean = true

  override def supportsSchemasInProcedureCalls(): Boolean = true

  override def supportsSchemasInTableDefinitions(): Boolean = true

  override def supportsSchemasInIndexDefinitions(): Boolean = true

  override def supportsSchemasInPrivilegeDefinitions(): Boolean = true

  override def supportsCatalogsInDataManipulation(): Boolean = true

  override def supportsCatalogsInProcedureCalls(): Boolean = true

  override def supportsCatalogsInTableDefinitions(): Boolean = true

  override def supportsCatalogsInIndexDefinitions(): Boolean = true

  override def supportsCatalogsInPrivilegeDefinitions(): Boolean = true

  override def supportsPositionedDelete(): Boolean = false

  override def supportsPositionedUpdate(): Boolean = false

  override def supportsSelectForUpdate(): Boolean = false

  override def supportsStoredProcedures(): Boolean = false

  override def supportsSubqueriesInComparisons(): Boolean = false

  override def supportsSubqueriesInExists(): Boolean = false

  override def supportsSubqueriesInIns(): Boolean = false

  override def supportsSubqueriesInQuantifieds(): Boolean = false

  override def supportsCorrelatedSubqueries(): Boolean = false

  override def supportsUnion(): Boolean = false

  override def supportsUnionAll(): Boolean = false

  override def supportsOpenCursorsAcrossCommit(): Boolean = false

  override def supportsOpenCursorsAcrossRollback(): Boolean = false

  override def supportsOpenStatementsAcrossCommit(): Boolean = false

  override def supportsOpenStatementsAcrossRollback(): Boolean = false

  override def getMaxBinaryLiteralLength: Int = 0

  override def getMaxCharLiteralLength: Int = 0

  override def getMaxColumnNameLength: Int = 0

  override def getMaxColumnsInGroupBy: Int = 0

  override def getMaxColumnsInIndex: Int = 0

  override def getMaxColumnsInOrderBy: Int = 0

  override def getMaxColumnsInSelect: Int = 0

  override def getMaxColumnsInTable: Int = 0

  override def getMaxConnections: Int = 0

  override def getMaxCursorNameLength: Int = 0

  override def getMaxIndexLength: Int = 0

  override def getMaxSchemaNameLength: Int = 0

  override def getMaxProcedureNameLength: Int = 0

  override def getMaxCatalogNameLength: Int = 0

  override def getMaxRowSize: Int = 0

  override def doesMaxRowSizeIncludeBlobs(): Boolean = true

  override def getMaxStatementLength: Int = 0

  override def getMaxStatements: Int = 0

  override def getMaxTableNameLength: Int = 0

  override def getMaxTablesInSelect: Int = 0

  override def getMaxUserNameLength: Int = 0

  override def getDefaultTransactionIsolation: Int = Connection.TRANSACTION_NONE

  override def supportsTransactions(): Boolean = false

  override def supportsTransactionIsolationLevel(level: Int): Boolean =
    level == Connection.TRANSACTION_NONE

  override def supportsDataDefinitionAndDataManipulationTransactions(): Boolean = false

  override def supportsDataManipulationTransactionsOnly(): Boolean = false

  override def dataDefinitionCausesTransactionCommit(): Boolean = false

  override def dataDefinitionIgnoredInTransactions(): Boolean = false

  override def getProcedures(
    catalog: String,
    schemaPattern: String,
    procedureNamePattern: String
  ): ResultSet =
    emptyResultSet(
      e("PROCEDURE_CAT"),
      e("PROCEDURE_SCHEM"),
      e("PROCEDURE_NAME"),
      e("RESERVED4"),
      e("RESERVED5"),
      e("RESERVED6"),
      e("REMARKS"),
      e("PROCEDURE_TYPE", Type.SHORT),
      e("SPECIFIC_NAME")
    )

  override def getProcedureColumns(
    catalog: String,
    schemaPattern: String,
    procedureNamePattern: String,
    columnNamePattern: String
  ): ResultSet =
    emptyResultSet(
      e("PROCEDURE_CAT"),
      e("PROCEDURE_SCHEM"),
      e("PROCEDURE_NAME"),
      e("COLUMN_NAME"),
      e("COLUMN_TYPE", Type.SHORT),
      e("DATA_TYPE", Type.INTEGER),
      e("TYPE_NAME"),
      e("PRECISION", Type.INTEGER),
      e("LENGTH", Type.INTEGER),
      e("SCALE", Type.SHORT),
      e("RADIX", Type.SHORT),
      e("NULLABLE", Type.SHORT),
      e("REMARKS"),
      e("COLUMN_DEF"),
      e("SQL_DATA_TYPE", Type.INTEGER),
      e("SQL_DATETIME_SUB", Type.INTEGER),
      e("CHAR_OCTET_LENGTH", Type.INTEGER),
      e("ORDINAL_POSITION", Type.INTEGER),
      e("IS_NULLABLE"),
      e("SPECIFIC_NAME")
    )

  override def getTables(
    catalog: String,
    schemaPattern: String,
    tableNamePattern: String,
    types: Array[String]
  ): ResultSet =
    singleRowResultSet(
      s("TABLE_CAT", "coralogix"),
      s("TABLE_SCHEM", "coralogix"),
      s("TABLE_NAME", "logs"),
      s("TABLE_TYPE", "TABLE"),
      s("REMARKS"),
      s("TYPE_CAT"),
      s("TYPE_SCHEM"),
      s("TYPE_NAME"),
      s("SELF_REFERENCING_COL_NAME"),
      s("REF_GENERATION")
    )

  override def getSchemas: ResultSet =
    singleRowResultSet(
      s("TABLE_SCHEM", "coralogix"),
      s("TABLE_CATALOG", "coralogix")
    )

  override def getCatalogs: ResultSet =
    singleRowResultSet(
      s("TABLE_CAT", "coralogix")
    )

  override def getTableTypes: ResultSet =
    singleRowResultSet(
      s("TABLE_TYPE", "TABLE")
    )

  override def getColumns(
    catalog: String,
    schemaPattern: String,
    tableNamePattern: String,
    columnNamePattern: String
  ): ResultSet =
    rt.unsafeRunTask(
      SqlQueryServiceClient
        .withTimeout(queryTimeout.seconds)
        .schema(SchemaRequest())
        .map(response =>
          ResultSetImpl(
            QueryResponse(
              response.columnDescriptors.zipWithIndex.map {
                case (column, index) =>
                  val columnType = ElasticsearchType.byName(column.`type`.name)
                  Row(
                    List(
                      str("coralogix"), // TABLE_CAT
                      str("coralogix"), // TABLE_SCHEM
                      str("logs"), // TABLE_NAME
                      str(column.name), // COLUMN_NAME
                      num(columnType.jdbcType.getVendorTypeNumber), // DATA_TYPE
                      str(columnType.jdbcType.getName), // TYPE_NAME
                      `null`, // COLUMN_SIZE
                      `null`, // BUFFER_LENGTH
                      `null`, // DECIMAL_DIGITS
                      `null`, // NUM_PREC_RADIX
                      num(DatabaseMetaData.columnNullableUnknown), // NULLABLE
                      `null`, // REMARKS
                      `null`, // COLUMN_DEF
                      `null`, // SQL_DATA_TYPE
                      `null`, // SQL_DATETIME_SUB
                      `null`, // CHAR_OCTET_LENGTH
                      num(index + 1), // ORDINAL_POSITION
                      str(""), // IS_NULLABLE
                      `null`, // SCOPE_CATALOG
                      `null`, // SCOPE_SCHEMA
                      `null`, // SCOPE_TABLE
                      `null`, // SOURCE_DATA_TYPE
                      str("NO"), // IS_AUTOINCREMENT
                      str("NO") // IS_GENERATEDCOLUMN
                    )
                  )
              },
              List(
                cd("TABLE_CAT"),
                cd("TABLE_SCHEM"),
                cd("TABLE_NAME"),
                cd("COLUMN_NAME"),
                cd("DATA_TYPE", Type.INTEGER),
                cd("TYPE_NAME"),
                cd("COLUMN_SIZE", Type.INTEGER),
                cd("BUFFER_LENGTH", Type.INTEGER),
                cd("DECIMAL_DIGITS"),
                cd("NUM_PREC_RADIX"),
                cd("NULLABLE"),
                cd("REMARKS"),
                cd("COLUMN_DEF"),
                cd("SQL_DATA_TYPE"),
                cd("SQL_DATETIME_SUB"),
                cd("CHAR_OCTET_LENGTH"),
                cd("ORDINAL_POSITION"),
                cd("IS_NULLABLE"),
                cd("SCOPE_CATALOG"),
                cd("SCOPE_SCHEMA"),
                cd("SCOPE_TABLE"),
                cd("SOURCE_DATA_TYPE"),
                cd("IS_AUTOINCREMENT"),
                cd("IS_GENERATEDCOLUMN")
              )
            ),
            "cursor",
            null
          )
        )
        .foldCauseM(
          _.foldLeft(ZIO.fail(new SQLException("Empty cause received"))) {
            case (_, Cause.Interrupt(_)) =>
              ZIO.fail(new SQLException("Introspection cancelled due to client request"))
            case (_, Cause.Fail(status)) =>
              ZIO.fail(
                new SQLException(
                  s"Introspection failed: ${Option(status.getDescription).getOrElse(status.getCode.name)}"
                )
              )
            case (_, Cause.Die(e)) => ZIO.fail(new SQLException(e))
          },
          ZIO.succeed(_)
        )
    )

  override def getColumnPrivileges(
    catalog: String,
    schema: String,
    table: String,
    columnNamePattern: String
  ): ResultSet =
    unsupported

  override def getTablePrivileges(
    catalog: String,
    schemaPattern: String,
    tableNamePattern: String
  ): ResultSet = unsupported

  override def getBestRowIdentifier(
    catalog: String,
    schema: String,
    table: String,
    scope: Int,
    nullable: Boolean
  ): ResultSet = unsupported

  override def getVersionColumns(catalog: String, schema: String, table: String): ResultSet =
    unsupported

  override def getPrimaryKeys(catalog: String, schema: String, table: String): ResultSet =
    emptyResultSet(
      e("TABLE_CAT"),
      e("TABLE_SCHEM"),
      e("TABLE_NAME"),
      e("COLUMN_NAME"),
      e("KEY_SEQ", Type.SHORT),
      e("PK_NAME")
    )

  override def getImportedKeys(catalog: String, schema: String, table: String): ResultSet =
    emptyResultSet(
      e("PKTABLE_CAT"),
      e("PKTABLE_SCHEM"),
      e("PKTABLE_NAME"),
      e("PKCOLUMN_NAME"),
      e("FKTABLE_CAT"),
      e("FKTABLE_SCHEM"),
      e("FKTABLE_NAME"),
      e("FKCOLUMN_NAME"),
      e("KEY_SEQ", Type.SHORT),
      e("UPDATE_RULE", Type.SHORT),
      e("DELETE_RULE", Type.SHORT),
      e("FK_NAME"),
      e("PK_NAME"),
      e("DEFERRABILITY", Type.SHORT)
    )

  override def getExportedKeys(catalog: String, schema: String, table: String): ResultSet =
    unsupported

  override def getCrossReference(
    parentCatalog: String,
    parentSchema: String,
    parentTable: String,
    foreignCatalog: String,
    foreignSchema: String,
    foreignTable: String
  ): ResultSet = unsupported

  override def getTypeInfo: ResultSet = {
    val columnDescriptors = List(
      cd("TYPE_NAME"),
      cd("DATA_TYPE"),
      cd("PRECISION"),
      cd("LITERAL_PREFIX"),
      cd("LITERAL_SUFFIX"),
      cd("CREATE_PARAMS"),
      cd("NULLABLE"),
      cd("CASE_SENSITIVE"),
      cd("SEARCHABLE"),
      cd("UNSIGNED_ATTRIBUTE"),
      cd("FIXED_PREC_SCALE"),
      cd("AUTO_INCREMENT"),
      cd("LOCAL_TYPE_NAME"),
      cd("MINIMUM_SCALE"),
      cd("MAXIMUM_SCALE"),
      cd("SQL_DATA_TYPE"),
      cd("SQL_DATETIME_SUB"),
      cd("NUM_PREC_RADIX")
    )

    val rows = ElasticsearchType.all.map(esType =>
      Row(
        List(
          str(esType.name), // TYPE_NAME
          num(esType.jdbcType.getVendorTypeNumber), // DATA_TYPE
          num(esType.precision), // PRECISION
          str("'"), // LITERAL_PREFIX
          str("'"), // LITERAL_SUFFIX
          `null`, // CREATE_PARAMS
          num(java.sql.DatabaseMetaData.typeNullableUnknown), // NULLABLE
          bool(
            esType == ElasticsearchType.TEXT || esType == ElasticsearchType.KEYWORD
          ) //CASE_SENSITIVE
          ,
          num(java.sql.DatabaseMetaData.typeSearchable), // SEARCHABLE
          bool(!esType.isSigned), // UNSIGNED_ATTRIBUTE
          bool(false), // FIXED_PREC_SCALE
          bool(false), // AUTO_INCREMENT
          `null`, // LOCAL_TYPE_NAME
          `null`, // MINIMUM_SCALE
          `null`, // MAXIMUM_SCALE
          `null`, // SQL_DATA_TYPE
          `null`, // SQL_DATETIME_SUB
          num(10) // NUM_PREC_RADIX
        )
      )
    )

    val q = QueryResponse(
      rows,
      columnDescriptors
    )

    ResultSetImpl(q, "cursor", null)
  }

  override def getIndexInfo(
    catalog: String,
    schema: String,
    table: String,
    unique: Boolean,
    approximate: Boolean
  ): ResultSet =
    emptyResultSet(
      e("TYPE_CAT"),
      e("TYPE_SCHEM"),
      e("TYPE_NAME"),
      e("NON_UNIQUE", Type.BOOLEAN),
      e("INDEX_QUALIFIER"),
      e("INDEX_NAME"),
      e("TYPE", Type.SHORT),
      e("ORDINAL_POSITION", Type.SHORT),
      e("COLUMN_NAME"),
      e("ASC_OR_DESC"),
      e("CARDINALITY", Type.LONG),
      e("PAGES", Type.LONG),
      e("FILTER_CONDITION")
    )

  override def supportsResultSetType(`type`: Int): Boolean =
    `type` == ResultSet.TYPE_FORWARD_ONLY

  override def supportsResultSetConcurrency(`type`: Int, concurrency: Int): Boolean =
    `type` == ResultSet.TYPE_FORWARD_ONLY && concurrency == ResultSet.CONCUR_READ_ONLY

  override def ownUpdatesAreVisible(`type`: Int): Boolean = false

  override def ownDeletesAreVisible(`type`: Int): Boolean = false

  override def ownInsertsAreVisible(`type`: Int): Boolean = false

  override def othersUpdatesAreVisible(`type`: Int): Boolean = false

  override def othersDeletesAreVisible(`type`: Int): Boolean = false

  override def othersInsertsAreVisible(`type`: Int): Boolean = false

  override def updatesAreDetected(`type`: Int): Boolean = false

  override def deletesAreDetected(`type`: Int): Boolean = false

  override def insertsAreDetected(`type`: Int): Boolean = false

  override def supportsBatchUpdates(): Boolean = false

  override def getUDTs(
    catalog: String,
    schemaPattern: String,
    typeNamePattern: String,
    types: Array[Int]
  ): ResultSet = unsupported

  override def getConnection: Connection = connection

  override def supportsSavepoints(): Boolean = false

  override def supportsNamedParameters(): Boolean = false

  override def supportsMultipleOpenResults(): Boolean = false

  override def supportsGetGeneratedKeys(): Boolean = false

  override def getSuperTypes(
    catalog: String,
    schemaPattern: String,
    typeNamePattern: String
  ): ResultSet =
    emptyResultSet(
      e("TYPE_CAT"),
      e("TYPE_SCHEM"),
      e("TYPE_NAME"),
      e("SUPERTYPE_CAT"),
      e("SUPERTYPE_SCHEM"),
      e("SUPERTYPE_NAME")
    )

  override def getSuperTables(
    catalog: String,
    schemaPattern: String,
    tableNamePattern: String
  ): ResultSet =
    emptyResultSet(
      e("TABLE_CAT"),
      e("TABLE_SCHEM"),
      e("TABLE_NAME"),
      e("SUPERTABLE_NAME")
    )

  override def getAttributes(
    catalog: String,
    schemaPattern: String,
    typeNamePattern: String,
    attributeNamePattern: String
  ): ResultSet = unsupported

  override def supportsResultSetHoldability(holdability: Int): Boolean =
    holdability == ResultSet.HOLD_CURSORS_OVER_COMMIT

  override def getResultSetHoldability: Int = ResultSet.HOLD_CURSORS_OVER_COMMIT

  override def getDatabaseMajorVersion: Int = 0

  override def getDatabaseMinorVersion: Int = 0

  override def getJDBCMajorVersion: Int = 4

  override def getJDBCMinorVersion: Int = 0

  override def getSQLStateType: Int =
    DatabaseMetaData.sqlStateSQL

  override def locatorsUpdateCopy(): Boolean = true

  override def supportsStatementPooling(): Boolean = false

  override def getRowIdLifetime: RowIdLifetime = RowIdLifetime.ROWID_UNSUPPORTED

  override def getSchemas(catalog: String, schemaPattern: String): ResultSet = unsupported

  override def supportsStoredFunctionsUsingCallSyntax(): Boolean = false

  override def autoCommitFailureClosesAllResultSets(): Boolean = false

  override def getClientInfoProperties: ResultSet = unsupported

  override def getFunctions(
    catalog: String,
    schemaPattern: String,
    functionNamePattern: String
  ): ResultSet =
    emptyResultSet(
      e("FUNCTION_CAT"),
      e("FUNCTION_SCHEM"),
      e("FUNCTION_NAME"),
      e("REMARKS"),
      e("FUNCTION_TYPE", Type.SHORT),
      e("SPECIFIC_NAME")
    )

  override def getFunctionColumns(
    catalog: String,
    schemaPattern: String,
    functionNamePattern: String,
    columnNamePattern: String
  ): ResultSet =
    emptyResultSet(
      e("FUNCTION_CAT"),
      e("FUNCTION_SCHEM"),
      e("FUNCTION_NAME"),
      e("COLUMN_NAME"),
      e("COLUMN_TYPE", Type.SHORT),
      e("DATA_TYPE", Type.INTEGER),
      e("TYPE_NAME"),
      e("PRECISION", Type.INTEGER),
      e("LENGTH", Type.INTEGER),
      e("SCALE", Type.SHORT),
      e("RADIX", Type.SHORT),
      e("NULLABLE", Type.SHORT),
      e("REMARKS"),
      e("CHAR_OCTET_LENGTH", Type.INTEGER),
      e("ORDINAL_POSITION", Type.INTEGER),
      e("IS_NULLABLE"),
      e("SPECIFIC_NAME")
    );

  override def getPseudoColumns(
    catalog: String,
    schemaPattern: String,
    tableNamePattern: String,
    columnNamePattern: String
  ): ResultSet =
    unsupported

  override def generatedKeyAlwaysReturned(): Boolean = false

  override def unwrap[T](iface: Class[T]): T = unsupported

  override def isWrapperFor(iface: Class[_]): Boolean = false
}
