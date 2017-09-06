PolicyEngineXACML
=================

Instructions
------------

Before building the included plugin projects, you will need to install locally the third party forgerock jars.
Navigate to the PolicyEngineXACML/ThirdParty directory and run the following two maven commands.  You should then be able to build the projects.

mvn install:install-file -Dfile=fedlib-10.0.1.jar -DgroupId=org.forgerock.opennam -DartifactId=fedlib -Dversion=10.0.1 -Dpackaging=jar
mvn install:install-file -Dfile=clientsdk-10.0.1.jar -DgroupId=org.forgerock.opennam -DartifactId=clientsdk -Dversion=10.0.1 -Dpackaging=jar
