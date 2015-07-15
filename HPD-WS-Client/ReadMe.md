HPD-WS-Client
=============
This Plugin was developed during 4.5.0 Release ans it's still in progress for future development.

CONNECT supported Release Version
=================================
CONNECT 4.5.0.

The client can be run using the following command: 
mvn exec:java -Dexec.mainClass=gov.hhs.onc.hpdclient.HPDClient  //this will print out the usage information
mvn exec:java -Dexec.mainClass=gov.hhs.onc.hpdclient.HPDClient -Dexec.args="professional givenName Thomas"

Healthcare Provider Directory (HPD) supports management of healthcare provider information, both individual and organizational, in a directory structure. The HPD is evolving as a replacement of UDDI currently getting used for eHealth exchange transactions.