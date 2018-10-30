PDMP
=======
Federal Health Architecture (FHA) developed a Prescription Drug Monitoring Program (PDMP) pilot for acccessing state prescription data through patient demographics queries.

License
-------
The PDMP pilot was developed under the [Revised BSD License](https://connectopensource.atlassian.net/wiki/x/mQCD).

Packages
--------
The pilot includes two sub projects, a PDMP library that provides services for accessing and parsing the state PDMP data and a PDMP Demo deployable for demonstrating the usage of the library by integrating it with CONNECT to produce expanded patient documents using the found patient prescription data and existing documents.  A user guide is provided for the PDMP demo in the Documentation folder.

Building from Source and Deploying
--------
The PDMP pilot is built using maven (version 3+) and Java 8.  The Demo was developed and tested with Wildfly 8.2.1 and CONNECT 5.2.  For quick build run the following from the PDMP folder: mvn clean install

Full directions for building and deploying are provided in the Demo User Guide.

History
-------
* MAR 2017 Initial Pilot Developed (CONNECT branch)
* OCT 2018 Plugin version release (with 4.6.0 plugins version)


