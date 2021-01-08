package com.coralogix.jdbc

import com.coralogix.sql.grpc.external.v1.SqlQueryService.{
  ColumnDescriptor,
  QueryParameter,
  QueryRequest,
  QueryResponse,
  Row,
  SchemaRequest,
  SchemaResponse
}
import com.coralogix.sql.grpc.external.v1.SqlQueryService.ZioSqlQueryService.SqlQueryServiceClient
import com.google.protobuf.struct.Value
import io.grpc.{ CallOptions, Status }
import scalapb.zio_grpc.SafeMetadata
import zio.test.Assertion.equalTo
import zio.{ IO, Layer, Task, ZIO, ZLayer, ZManaged }
import zio.test.{ assert, suite, testM, DefaultRunnableSpec }

import java.sql.{ ResultSet, Timestamp }

object PreparedStatementTest extends DefaultRunnableSpec {

  case class Service[R, Context](req: QueryRequest, res: QueryResponse)
      extends SqlQueryServiceClient.ZService[R, Context] {

    override def runQuery(request: QueryRequest): ZIO[R with Context, Status, QueryResponse] =
      if (request.query == req.query && request.parameters.size == req.parameters.size)
        ZIO.succeed(res)
      else
        ZIO.fail(Status.INVALID_ARGUMENT)

    override def withMetadataM[C](
      headersEffect: ZIO[C, Status, SafeMetadata]
    ): SqlQueryServiceClient.ZService[R, C] = Service(req, res)

    override def withCallOptionsM(
      callOptions: IO[Status, CallOptions]
    ): SqlQueryServiceClient.ZService[R, Context] = Service(req, res)

    override def mapCallOptionsM(
      f: CallOptions => IO[Status, CallOptions]
    ): SqlQueryServiceClient.ZService[R, Context] = Service(req, res)

    override def schema(request: SchemaRequest): ZIO[R with Context, Status, SchemaResponse] =
      ZIO.fail(Status.NOT_FOUND)
  }

  def client(req: QueryRequest, res: QueryResponse): Layer[Throwable, SqlQueryServiceClient] =
    ZLayer.succeed(Service(req, res))

  def connection(req: QueryRequest, res: QueryResponse) =
    ZManaged.makeEffect(DriverLogic.connect(client(req, res), "", 30))(_.close)

  def resultFrom[A](rs: ResultSet, f: ResultSet => A): Task[List[A]] =
    Task {
      var res = List.empty[A]
      while (rs.next())
        res = res :+ f(rs)
      res
    }

  override def spec =
    suite("PrepareStatement")(
      testM("should set parameters and return data")(
        connection(
          QueryRequest(
            "select id, cx.branchId, cx.timestamp from logs where cx.branchId <> ? and cx.timestamp = ?",
            List(
              QueryParameter(Some(Value().withStringValue("branch"))),
              QueryParameter(Some(Value().withNumberValue(1000L)))
            )
          ),
          QueryResponse(
            List(
              Row(
                List(
                  Value().withNumberValue(1),
                  Value().withStringValue("b1"),
                  Value().withNumberValue(1000L)
                )
              ),
              Row(
                List(
                  Value().withNumberValue(2),
                  Value().withStringValue("b2"),
                  Value().withNumberValue(1000L)
                )
              )
            ),
            List(
              ColumnDescriptor("id", "id"),
              ColumnDescriptor("cx.branchId", "bi"),
              ColumnDescriptor("cx.timestamp", "cx.timestamp")
            )
          )
        ).use(con =>
          for {
            ps <-
              Task(
                con.prepareStatement(
                  "select id, cx.branchId, cx.timestamp from logs where cx.branchId <> ? and cx.timestamp = ?"
                )
              )
            _         <- Task(ps.setObject(1, "branch"))
            _         <- Task(ps.setTimestamp(2, new Timestamp(1000L)))
            _         <- Task(ps.execute())
            resultSet <- Task(ps.getResultSet)
            res <- resultFrom(
                     resultSet,
                     rs =>
                       (
                         rs.getInt("id"),
                         rs.getString("bi"),
                         rs.getTimestamp("cx.timestamp").getTime
                       )
                   )
            _ <- Task(resultSet.close())
            _ <- Task(ps.close())
          } yield assert(res)(equalTo(List((1, "b1", 1000L), (2, "b2", 1000L))))
        )
      )
    )
}
