# Coralogix database driver Usage

Now you can investigate logs in our favorite database tool.

## Using the driver

These are the steps you need to do:

1. Download the latest driver - [coralogix-jdbc-0.2.jar](https://repo1.maven.org/maven2/com/coralogix/coralogix-jdbc/0.2/coralogix-jdbc-0.2.jar)
2. Install driver
3. Create datasource
4. Test it with `select * from logs`

We will show you how to do it for three popular database tools. Procedure with other tools should be similar.

### DataGrip

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
   `Settings` -> `API Access` tab -> `Logs API Key`
10. Click on `Apply` then `OK`

### DBeaver

1. Download the latest driver
2. In the menu open `Database` -> `Driver manager` and click on `New` button
3. In `Driver Name` field write `Coralogix`
4. Click on `Add File` button and pick the driver file you downloaded
5. Click on `Find Class` button it should show you `com.coralogix.jdbc.Driver` in
   `Driver Class` field, click on it
6. Click on `OK`
7. Click on `New Database Connection` in the menu (plug with `+` icon)
8. Type `coralogix` into search box in the left tab choose `All` 
   and choose `Coralogix` driver and click on `Next`
9. Set `JDBC URL` to
   `jdbc:coralogix://grpc-api.coralogix.com` for **Europe** or
   `jdbc:coralogix://grpc-api.app.coralogix.in` for **India**
10. Click on `Driver properties` tab input your `apiKey`. You will get it from Coralogix dashboard
    `Settings` -> `API Access` tab -> `Logs API Key`
11. In the `Main` tab click on `Connection Details` button and choose better `Connection Name` e.g. `Coralogix`
11. Click on `Finish`
12. `Coralogix` connection in `Database Navigator` was created

### Tableau

1. Download the latest driver
2. Place the .jar files in the folder for your operating system. (You need to create the folder if it doesn't already exist.)
   
   | OS      | Path                             |
   | ------- | -------------------------------- |
   | Windows | C:\Program Files\Tableau\Drivers |
   | Mac     | ~/Library/Tableau/Drivers        |
   | Linux   | /opt/tableau/tableau_driver/jdbc |
3. Copy [coralogix.tdc](coralogix.tdc) file in `~/Documents/My Tableau Repository/Datasources`
   For more details consult [Tableau documentation](https://kb.tableau.com/articles/howto/using-a-tdc-file-with-tableau-server)
4. Create file `coralogix.properties` and add there line with your `apiKey`.
   You will get it from Coralogix dashboard `Settings` -> `API Access` tab -> `Logs API Key`
   ```
   apiKey=<YOUR API KEY>
   ```
5. On the main screen in `To a Server` section choose `Other Databases (JDBC)`
6. Set `URL` to
   `jdbc:coralogix://grpc-api.coralogix.com` for **Europe** or
   `jdbc:coralogix://grpc-api.app.coralogix.in` for **India**
   Set `Dialect` to `MySQL`
   Leave `Username` and `Password` blank
   Click on `Browse` next `Properties file` and choose `coralogix.props` you created
7. Click on `Sign In`

## Queries

Simplest query:
```
SELECT * FROM logs
```

Also Elasticsearch queries are supported, you can write them in `QUERY` function:
```
SELECT * FROM logs 
  WHERE
    QUERY('coralogix.metadata.subsystemName:AAA OR coralogix.metadata.subsystemName:BBB');
```