Plugins
=======

This repository has CONNECT HIEOS Adapter interfaces which supports to submit a Document to HIEOS repository. The CONNECT can use these adapters to do a Document lookup(DocumentQuery) using DocumentRegistry interface and CONNECT can retrieve a document from HIOES repository using DocRepository Adapter Repository.

Build:

Navigate to Directory where code is downloaded.

mvn clean install

Deploy the Artifacts to Glassfish

Login to Glassfish Admin console

Manually deploy the AdapterDocRepository2Soap12Web-4.3.0-SNAPSHOT.war and AdapterDocRegistry2Soap12Web-4.3.0-SNAPSHOT.war.

Test HIEOS interfaces

Navigate to CONNECT installed directory.

Deploy CONNECT ear.
