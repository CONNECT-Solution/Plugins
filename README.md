Plugins
=======

XDSBPlugin
----------

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

HPD-WS-Client:
--------------
The client cna be run using the following command: 
mvn exec:java -Dexec.mainClass=gov.hhs.onc.hpdclient.HPDClient  //this will print out the usage information
mvn exec:java -Dexec.mainClass=gov.hhs.onc.hpdclient.HPDClient -Dexec.args="professional givenName Thomas"

FHIRAdapters
------------

The fhir Adapters are developed for PatientDiscovery,DocumentQuery, DocumentRepository and DocumentSubmission services. This plugin was tested using CONNECT Initiating gateway initiates the above exchange service requests and send it to CONNECT Responding gateway using the fhir Reference adapters which interops with fhir HAPI server. It was mainly used for HIMSS Demo 2015.

FHIRRestServer
---------------

The fhir Binary Rest Server was created since the HAPI server does not provide Binary storage. This functionality is used by DocumentSubmission services to store the Binary document and provide a Binary reference for retrieval. The Document Retrieve service service use this reference to retrieve documents and the storage is file based. This functionality was also mainly implemented for HIMSS Demo 2015.

FHIRSoapTests:
--------------

The fhir Soap Tests were developed to persist data in HAPI fhir server for the exchange services PatientDiscovery, DocumentQuery and DocumentRetrieve.

GenericFileTransfer:
--------------------

This project was used with HIEM functionality for Pre CONNECT 3.3 Releases. HIEM was deprecated since 4.0.


