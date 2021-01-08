# Coralogix database driver Usage

Now you can investigate logs in our favorite database tool.

These are the steps you need to do:

1. Download the latest driver - [coralogix-jdbc-0.2.jar](https://repo1.maven.org/maven2/com/coralogix/coralogix-jdbc/0.2/coralogix-jdbc-0.2.jar)
2. Install driver
3. Create datasource
4. Test it with `select * from logs`

We will show you how to do it for three popular database tools. Procedure with other tools should be similar.

## DataGrip

1. Download the latest driver
2. Click on `+` icon in the database menu and choose `Driver`
3. Into `Name` field write `Coralogix`
4. Click on `+` under `Driver Files` and pick the driver file you downloaded
5. Open `Class` picker and pick `com.coralogix.jdbc.Driver`
6. Click on `Apply` then `OK`
7. Click on `+` icon in the database menu -> choose `Data Source` -> choose `Coralogix`
8. In `General` tab change the url to
   `jdbc:coralogix://grpc-api.coralogix.com` for **Europe** or
   `jdbc:coralogix://grpc-api.app.coralogix.in` for **India**
9. In `Advanced` tab input your `apiKey`. You will get it from Coralogix dashboard
   `Settings` -> `API Access` tab -> `Logs API Key`.
10. Click on `Apply` then `OK`

## DBeaver

1. Download the latest driver
2. In the menu open `Database` -> `Driver manager` and click on `New` button
3. In `Driver Name` field write `Coralogix`
4. Click on `Add File` button and pick the driver file you downloaded
5. Click on `Find Class` button it should show you `com.coralogix.jdbc.Driver` in
   `Driver Class` field, click on it
6. Click on `OK`
7. Click on `New Database Connection` in the menu (plug with `+` icon)
8. Type `coralogix` into search box and choose `Coralogix` driver and click on `Next`
9. Set `JDBC URL` to
   `jdbc:coralogix://grpc-api.coralogix.com` for **Europe** or
   `jdbc:coralogix://grpc-api.app.coralogix.in` for **India**
10. Click on `Driver properties` tab input your `apiKey`. You will get it from Coralogix dashboard
    `Settings` -> `API Access` tab -> `Logs API Key`.
11. In the `Main` tab click on `Connection Details` button and choose better `Connection Name` e.g. `Coralogix`
11. Click on `Finish`
12. `Coralogix` connection in `Database Navigator` was created

## Tableau
