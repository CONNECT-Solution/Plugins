FHIRAdapters
============
FHIRAdapters are implemented during CONNECT 4.5.0 and they are still in progress for future development.

CONNECT supported Release Version
=================================
CONNECT 4.5.0

Build FHIRAdapters
==================
Navigate to <Install_Directory>\FHIRAdapters. Execute the following maven command

mvn clean install

Deploy
======
Navigate to FHIRAdapters\target folder.
Deploy FHIRAdapter war.

The fhir Adapters are developed for PatientDiscovery,DocumentQuery, DocumentRetrieve and DocumentSubmission exchange services. This plugin was tested using CONNECT Initiating gateway initiating the above exchange service requests to CONNECT Responding gateway using the fhir Reference adapters which interops with fhir HAPI server. It was mainly used for HIMSS Demo 2015. The AdapterMPI, DocRegistry, DocRepository and AdapterComponentXDR endpoints should be updated to fhir Adapter endpoints.