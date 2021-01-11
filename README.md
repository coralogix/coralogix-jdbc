# JDBC driver

JDBC driver connecting to es-sql service via gRPC

# Usage

For using this driver with various db tools [click here](USAGE.md)

# For developers
Point JDBC url to es-sql service 

for **Europe**
```
jdbc:coralogix://grpc-api.coralogix.com 
```
for **India**
```
jdbc:coralogix://grpc-api.app.coralogix.in
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