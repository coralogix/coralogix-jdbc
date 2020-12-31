package com.coralogix.jdbc

import com.coralogix.sql.grpc.external.v1.SqlQueryService.ZioSqlQueryService.SqlQueryServiceClient
import io.grpc.{ ManagedChannelBuilder, Metadata }
import scalapb.zio_grpc.{ SafeMetadata, ZManagedChannel }
import zio._

import java.sql.{ Connection, DriverPropertyInfo, SQLException }
import java.util.Properties

object DriverLogic {

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
    getProperty(properties, timeoutProperty, 30)(
      _.toIntOption
        .filter(_ > 0)
        .getOrElse(throw new SQLException("Timeout has to be positive integer."))
    )

  def connect(url: String, properties: Properties): Connection = {

    val (host, port) =
      try {
        val Array(host, portStr) = url.stripPrefix(URL_PREFIX).split(":")
        val port = portStr.toInt
        (host, port)
      } catch {
        case _: Throwable => throw new SQLException("Error parsing jdbc url")
      }

    val apiKey = Option(properties.getProperty(apiKeyProperty.name))
      .filterNot(_.isEmpty)
      .getOrElse(
        throw new SQLException("Please specify apiKey property")
      )

    connect(layer(host, port, apiKey, getTls(properties)), url, getTimeout(properties))
  }

  def connect(
    client: Layer[Throwable, SqlQueryServiceClient],
    url: String,
    queryTimeout: Int
  ): Connection =
    new ConnectionImpl(Runtime.unsafeFromLayer(client), url, queryTimeout)

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
    property("timeout", false, "Request timeout in seconds.\nDefault: 30")

  def getPropertyInfo(): Array[DriverPropertyInfo] =
    Array(apiKeyProperty, tlsProperty, timeoutProperty)

}
