Eventjuggler Services
=====================


Prerequisites
=============

- Java Development Kit 7
- Recent Git client
- Recent Maven 3
- JBoss EAP 6.1.0.Alpha or 6.1.0.Beta


Installation
============

The build component creates a package of the services. By default an overlay package is created. The overlay package only contains
the file that is added/changed to a standard JBoss AS installation. It is also possible to create a full package by specifying a
location for the JBoss EAP download.

To create an overlay and install it on an existing JBoss EAP installation run:

    mvn clean install
    cp -r build/target/jboss-eap-6.1/* <JBOSS_HOME>/

Alternatively create a full package with:

    mvn clean install -Djboss.zip=<JBOSS EAP ZIP>


Distribution
============

A distribution can be created with the 'release' profile. This creates zip and tar.gz archives. When releasing make sure you don't
specify '-Djboss.zip' as we should only release the overlay when using EAP.

To create a distribution run:

    mvn clean install -Prelease


Testsuite
=========

The testsuite contains a set of integration tests. The tests can be execute in either managed or remote mode. In managed mode the
tests have been configured to use the full package built by the build module.

To run the testsuite in managed mode, run:

    mvn -Pit-managed -Djboss.zip=<PATH TO JBOSS EAP ZIP> clean install

In managed mode you have to provide "-Djboss.zip" as it requires a full package (see the installation section).

To run the testsuite in remote mode, first start a JBoss EAP with EventJuggler Services installed, and run:

    mvn -Pit-remote clean install

