PerformanceTestTools
====================
This Plugin was developed during 4.0 Release.

CONNECT supported Release Version
=================================
CONNECT 4.0

Build PerformanceTestTools
==========================
Navigate to <Install_Directory>\PerformanceTestTools. Build using following maven command "mvn clean install"

A jar should be generated in <Install_Directory>\PerformanceTestTools\target. This jar needs to built in CONNECT ear to perform the tests.

Tips
====

The files in the Performance Test Tools are used for performance testing CONNECT 4.0 on glassfish, Weblogic, and Websphere.

The project located in CONNECTLoadTestJar is buildable using Netbeans clean and build.
The resulting jar must be placed within the deployed ear directory and the server restarted.
gfish: /domain/domain1/applications/CONNECT-x.x.x-snapshot/lib
weblogic: /base_domain/servers/CONNECT/tmp/_WL_user/CONNECT-WL-4.0.0-SNAPSHOT/udwrax/lib/
websphere: /repository/org/connectopensource/CONNECT/4.0/ear/lib
*In websphere the lib/CONNECTLoadTestJar.jar must be added to the MANIFEST.MF
*In websphere xpp3 and xstream should be removed from the EAR lib

The config directory contents should be copied into $NHINC_PROPERTIES_DIR
