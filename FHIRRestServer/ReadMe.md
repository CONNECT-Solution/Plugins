FHIRRestServer
==============
FHIRRestServer was implemented during CONNECT 4.5.0 and it's still in progress for future development.

CONNECT supported Release Version
=================================
CONNECT 4.5.0

Build FHIRRestServer
====================
Navigate to <Install_Directory>\FHIRRestServer. Build using following maven command

mvn clean install

Deploy
======
Navigate to FHIRRestServer\target folder.
Deploy FHIRRestServer war.

The fhir Binary Rest Server was created since the HAPI server does not provide Binary storage. This functionality is used by DocumentSubmission services to store the Binary document and provide a Binary reference for retrieval. The file system storage is currently implemented for Binary documents storage.The Document Retrieve service use this reference to retrieve documents from the file system. This functionality was also mainly implemented for HIMSS Demo 2015.
