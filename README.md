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

  mvn clean install -Djbossas.zip=<JBOSS AS ZIP>
