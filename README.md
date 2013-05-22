Eventjuggler Services
=====================


Prerequisites
=============

- Java Development Kit 7
- Recent Git client
- Recent Maven 3


Installation
============

The build component downloads and extracts WildFly 8.0.0.Alpha1. Then it installs EventJuggler Services and the PicketLink subsystem into the
WildFly installation.

To build with WildFly run:

    mvn clean install


Distribution
============

A distribution can be created with the release profile.

To create a distribution run:

    mvn clean install -Prelease


Testsuite
=========

The testsuite contains a set of integration tests. The tests can be execute in either managed or remote mode. In managed mode the
tests have been configured to use the WildFly installation created by the build module.

To run the testsuite in managed mode, run:

    mvn -Pit-managed clean install

To run the testsuite in remote mode, first start WildFly with EventJuggler Services and PicketLink subsystem installed, and run:

    mvn -Pit-remote clean install

