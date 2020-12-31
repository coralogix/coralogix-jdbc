# JDBC driver

JDBC driver connecting to es-sql service via gRPC

## Usage
Point Jbdc url to es-sql service
```
"jdbc:coralogix://localhost:9090"
```
## Properties
| Property      | Description                      | Required | Default |
| ------------- | -------------------------------- | -------- | ------- |
| **apiKey**    | Coralogix customer's `apiKey`    | true     |         |
| **tls**       | For local testing set to `false` | false    | `true`  |
| **timeout**   | Request timeout in seconds       | false    | 30      |

## Build

```
sbt assembly
```
You will find built jar in `target/scala-2.13/coralogix-driver.jar`