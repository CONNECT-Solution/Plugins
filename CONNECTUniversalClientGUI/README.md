CONNECTUniversalClientGUI
=========================

History
=======
Before 4.0 Release CONNECTUniversalClientGUI war was built and deployed along with CONNECT wars. Since CONNECT 4.0 Release the UniversalClientGUI was packaged within CONNECT-GUI ear and supported only on Glassfish Application servers. During CONNECT 4.5.0 Release CONNECTUniversalClientGUI was moved into Plugins repository and it can be built as a separate war and deployed on Glassfish Application server which is supported by CONNECT team. 

CONNECT supported Release Version
=================================
CONNECT 1.0 through CONNECT 4.5

Building CONNECTUniversalClientGUI
==================================
Navigate to <Install Folder>/CONNECTUniversalClientGUI. Execute the following maven command.

mvn clean install

Deploy
=======

1. Deploy CONNECTUniversalClientGUI war
2. Deploy CONNECT ear.

CONNECTUniversalClientGUI can be used as a client to execute sequential Transaction of exchange services Patient Discovery,Document Query and Document Retrieve services.All the exchange services should be operated in Standard mode.The Patient Discovery can be performed by providing Patient LastName and FirstName.The Patient look up will be performed on local mpi system and if the patient is available the patient will be listed.The user can perform PatientSearch Query by clicking on Discover Patient button and the Nwhin request will be send to all Organizations listed in UDDI since 3.3 Release. Once the Patient Discovery is successful then the user can do Document Query by clicking on Document Query button from DOcument tab and list  the documents available from the organizations in UDDI where the request was targeted. This document can be retrieved by clicking on the documents list shown in GUI and the document can be viewed from browser. It's recommended to use two CONNECT Gateways.

