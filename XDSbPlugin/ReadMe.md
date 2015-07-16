XDSBPlugin
==========
This Plugin was developed during 4.2 Release

CONNECT supported Release Version
=================================
CONNECT 4.2.0 through CONNECT 4.5.0. But the Plugins are not part of Release Testing.

Build XDSBPlugin
================
Navigate to <Install_Directory>\AdapterDocRegistry2Soap12Web. Build using following maven command "mvn clean install"
Navigate to <Install_Directory>\AdapterDocRepository2Soap12Web. Build using the following command "mvn clean install"

Deploy
======

Login to Glassfish Admin console

Manually deploy the AdapterDocRepository2Soap12Web-4.x.x-SNAPSHOT.war and AdapterDocRegistry2Soap12Web-4.x.x-SNAPSHOT.war.

Test HIEOS interfaces

Navigate to CONNECT installed directory.

Deploy CONNECT ear.

XDSB Plugin was created during 4.2 and the HIEOS Adapters were upgraded to use CONNECT CXF clients. HIEOS Registry and Repository can be set up for CONNECT Gateways and these HIEOS Adapter interfaces will route the CONNECT Gateway messages to HIEOS. The HIEOS DocRegistry and DocRepository endpoints should be updated in internalConnectionInfo files to utilize HIEOS Registry/Repository.
