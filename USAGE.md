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
3. Copy the [coralogix.tdc](coralogix.tdc) file to `~/Documents/My Tableau Repository/Datasources`
   For more details consult [Tableau documentation](https://kb.tableau.com/articles/howto/using-a-tdc-file-with-tableau-server)
4. Create a file `coralogix.properties` and add an entry with your `apiKey`.
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

Number of columns in logs table is dynamic and can ago into few hundreds, so it is better to ask
for specific columns.

### Log Table

The only supported table at the moment is `log` table. There might be more tables in the future.

You can navigate objects like `coralogix.metadata.subsystemName`.

```
SELECT text, coralogix.metadata.subsystemName FROM logs
```


<!-- FIXME I would like to add standard columns, what are they and also metion nonstandard one

| Column  | Type                             | Description |
| ------- | -------------------------------- | ----------- |
|  | |
|  | |
|  | |

-->

To make common queries easier you can select also from table
```
SELECT * FROM logs.<appName>.<subsystem>
```
and query will be translated to
```
SELECT * FROM logs WHERE
coralogix.metadata.applicationName = <appName> AND
coralogix.metadata.subsystemName = <subsystem>
```


## Fulltext search

The Coralogix SQL support is based on the 
[OpenDistro SQL support](https://opendistro.github.io/for-elasticsearch-docs/docs/sql/sql-full-text/).

One of the caveat of full-text search is that is works `text` indices but does not work for `keyword` indices.

Following are examples of full-text search operators that are supported:

### Match

If you want to search for text in a single field, use MATCHQUERY or MATCH_QUERY functions.

First parameter is the field name second one is the search query.
```
SELECT text, coralogix.metadata.severity
FROM logs
WHERE MATCH_QUERY(text, 'healthcheck')
```
Alternate syntax:
```
SELECT text, coralogix.metadata.severity
FROM logs
WHERE text = MATCH_QUERY('healthcheck')
```

### Multi match
If you want to search for text in multiple fields, use MULTI_MATCH, MULTIMATCH, or MULTIMATCHQUERY functions.

In this example, we search for `health` in either the text or lastname fields:

```
SELECT kubernetes.labels.app, kubernetes.labels.controller-uid
FROM logs
WHERE MULTI_MATCH('query'='prod', 'fields'='*labels*');
```

### Elasticsearch queries

Elasticsearch queries are supported, you can write them in `QUERY` function:
```
SELECT * FROM logs 
  WHERE
    QUERY('coralogix.metadata.subsystemName:AAA OR coralogix.metadata.subsystemName:BBB');
```

### Match phrase

If you want to search for exact phrases, use MATCHPHRASE, MATCH_PHRASE, or MATCHPHRASEQUERY functions.

```
SELECT text, coralogix.metadata.severity 
FROM logs
WHERE 
   MATCH_PHRASE(text, 'DELETE FROM templates')
```

<!-- FIXME this needs to be implemented in es-sql-api in es json parser
### Score query

You can get relevance score along with every matching document, by using SCORE, SCOREQUERY, or SCORE_QUERY functions.

The first argument is the MATCH_QUERY expression. The second argument is a floating point number to boost the score 
(if not set default value is 1.0).

```
SELECT text, kubernetes.labels.app, _score
FROM logs
WHERE SCORE(MATCH_QUERY(text, '_updateTemplates'), 2) OR
SCORE(MATCH_QUERY(kubernetes.labels.app, 'prod'), 10)
ORDER BY _score desc
```
-->