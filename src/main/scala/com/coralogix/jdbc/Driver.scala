package com.coralogix.jdbc

import com.coralogix.sql.grpc.external.v1.SqlQueryService.{ ValidateRequest, ValidateResponse }
import com.coralogix.sql.grpc.external.v1.SqlQueryService.ZioSqlQueryService.SqlQueryServiceClient
import io.grpc.{ ManagedChannelBuilder, Metadata }
import scalapb.zio_grpc.{ SafeMetadata, ZManagedChannel }
import zio.Exit.{ Failure, Success }
import zio._

import java.sql.{ Connection, DriverPropertyInfo, SQLException, SQLFeatureNotSupportedException }
import java.util.Properties

class Driver extends java.sql.Driver {

  val URL_PREFIX = "jdbc:coralogix://"

  def layer(
    host: String,
    port: Int,
    apiKey: String,
    tls: Boolean
  ): Layer[Throwable, SqlQueryServiceClient] = {
    val AuthorizationKey =
      Metadata.Key.of("Authorization", io.grpc.Metadata.ASCII_STRING_MARSHALLER)
    val XCoralogixAPIKey =
      Metadata.Key.of("X-Coralogix-API", io.grpc.Metadata.ASCII_STRING_MARSHALLER)

    val metadata = for {
      sm <- SafeMetadata.make
      _  <- sm.put(AuthorizationKey, s"Bearer $apiKey")
      _  <- sm.put(XCoralogixAPIKey, "sql")
    } yield sm

    SqlQueryServiceClient.live(
      ZManagedChannel(
        if (tls)
          ManagedChannelBuilder.forAddress(host, port)
        else
          ManagedChannelBuilder.forAddress(host, port).usePlaintext()
      ),
      headers = metadata
    )
  }

  def getProperty[A](properties: Properties, key: DriverPropertyInfo, default: A)(
    convert: String => A
  ) =
    Option(properties.getProperty(key.name))
      .filter(_.nonEmpty)
      .fold(default)(convert)

  def getTls(properties: Properties): Boolean =
    getProperty(properties, tlsProperty, true)(
      _.toBooleanOption.getOrElse(
        throw new SQLException("tls property has to be `true` or `false`")
      )
    )

  def getTimeout(properties: Properties): Int =
    getProperty(properties, timeoutProperty, 60)(
      _.toIntOption
        .filter(_ > 0)
        .getOrElse(throw new SQLException("Timeout has to be positive integer."))
    )

  def connect(url: String, properties: Properties): Connection = {

    val tls = getTls(properties)

    val (host, port) =
      url.stripPrefix(URL_PREFIX).split(":") match {
        case Array(host, port) =>
          (host, port.toIntOption.getOrElse(throw new SQLException("Error parsing port jdbc url")))
        case Array(host) => (host, if (tls) 443 else 80)
        case _           => throw new SQLException("Error parsing jdbc url, multiple \":\"")
      }

    val apiKey = Option(properties.getProperty(apiKeyProperty.name))
      .filterNot(_.isEmpty)
      .getOrElse(
        throw new SQLException("Please specify apiKey property")
      )

    val runtime = Runtime.unsafeFromLayer(layer(host, port, apiKey, tls))
    // validate connection to server
    runtime.unsafeRunSync(SqlQueryServiceClient.validate(ValidateRequest())) match {
      case Success(ValidateResponse(true, _, _)) => ()
      case Success(ValidateResponse(false, error, _)) =>
        throw new SQLException(error)
      case Failure(cause) =>
        throw new SQLException(cause.squashTraceWith(_.asException).getMessage)
    }
    connect(runtime, url, getTimeout(properties))
  }

  def connect(
    runtime: Runtime[SqlQueryServiceClient],
    url: String,
    queryTimeout: Int
  ): Connection =
    new ConnectionImpl(runtime, url, queryTimeout)

  def acceptsURL(url: String): Boolean =
    url != null && url.startsWith(URL_PREFIX)

  def property(name: String, required: Boolean, description: String): DriverPropertyInfo = {
    val p = new DriverPropertyInfo(name, null)
    p.required = required
    p.description = description
    p
  }

  val apiKeyProperty = property("apiKey", true, "Coralogix api-key")
  val tlsProperty = property("tls", false, "TLS 'true' or 'false'.\nDefault: true")
  val timeoutProperty =
    property("timeout", false, "Request timeout in seconds.\nDefault: 60")

  def getPropertyInfo(url: String, info: Properties): Array[DriverPropertyInfo] =
    Array(apiKeyProperty, tlsProperty, timeoutProperty)

  def getMajorVersion(): Int = 1

  def getMinorVersion(): Int = 0

  def getParentLogger(): java.util.logging.Logger =
    throw new SQLFeatureNotSupportedException()

  def jdbcCompliant(): Boolean = false
}
