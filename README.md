Eventjuggler Services
=====================


Prerequisites
=============

- Java Development Kit 6
- Recent Git client
- Recent Maven 3
- JBoss AS 7.1.1.Final (or 7.1.3.Final)


Installation
============

The build component creates a package of the services. By default an overlay package is created. The overlay package only contains
the file that is added/changed to a standard JBoss AS installation. It is also possible to create a full package by specifying a
location for the JBoss AS download.

To create an overlay and install it on an existing JBoss AS installation run:

  mvn clean install
  cp -r build/target/jboss-as-7.1.3.Final/* <JBOSS_HOME>/

Alternatively create a full package with:

  mvn clean install -Djboss.zip=<JBOSS AS ZIP>


Testsuite
=========

The testsuite contains integration tests that uses the EventJuggler Services deployed to AS7. The tests can be run in managed mode, which 
uses the full package, or alternatively an AS7 instance can be configured and started manually and tests run in remote mode.

To run the testsuite in managed mode, run:

mvn -Pit-managed -Djboss.zip=<JBOSS AS ZIP> clean install

In managed mode you either have to provide "-Djboss.zip=<JBOSS AS ZIP>" as it requires a full package (see the installation section).

To run the testsuite in remote mode, first start a JBoss AS with the EventJuggler Services sub-systems enabled, and run:

mvn -Pit-remote -Djboss.zip=<JBOSS AS ZIP> clean install

