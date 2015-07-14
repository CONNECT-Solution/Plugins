CONNECT-Hub
===========


to initialize the database:
```mvn sql:execute -Pdefault,initialize```

to upgrade the database:
```mvn liquibase:update -Pdefault```

to run the web application
```mvn jetty:run ```
