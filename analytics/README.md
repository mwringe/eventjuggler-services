Analytics Service
=================


Enable using JAR packaging
--------------------------

Add the following dependencies to the WAR pom.xml:

<dependency>
  <groupId>org.eventjuggler</groupId>
  <artifactId>analytics-api</artifactId>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>org.eventjuggler</groupId>
  <artifactId>analytics-impl</artifactId>
  <scope>provided</scope>
</dependency>

Add the following filter to the WAR web.xml:

<filter>
  <filter-name>analyticsFilter</filter-name>
  <filter-class>org.eventjuggler.analytics.AnalyticsFilter</filter-class>
</filter>
<filter-mapping>
  <filter-name>analyticsFilter</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>


Enable using AS7 service
------------------------

Deploy Analytics Service to JBoss 7.1.3.Final by running install.sh

Add the following to the WAR WEB-INF/jboss-deployment-structure.xml:

<jboss-deployment-structure>
  <deployment>
    <dependencies>
      <module name="org.eventjuggler.analytics" />
    </dependencies>
  </deployment>
</jboss-deployment-structure>


Install Analytics Web Console
-----------------------------

Copy analytics-web.war to JBoss deployments
