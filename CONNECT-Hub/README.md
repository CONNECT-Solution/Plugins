CONNECT-Hub
===========
This Plugin was developed for HIMSS Demo 2013.

CONNECT supported Release Version
=================================
CONNECT 4.0

Build CONNECT-Hub
================
Navigate to <Install_Directory>\CONNECT-Hub. Build using following maven command "mvn clean install"

useful commands for operations mentioned:
=========================================
to initialize the database:
```mvn sql:execute -Pdefault,initialize```

to upgrade the database:
```mvn liquibase:update -Pdefault```

to run the web application
```mvn jetty:run ```

CONNECT-Hub was developed during the HIMSS Demo 2013 and supported during 4.0 Release. This project use liquibase database and juddi.


