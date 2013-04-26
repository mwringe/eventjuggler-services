<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:xalan="http://xml.apache.org/xalan" 
   xmlns:j="urn:jboss:domain:1.3"
   version="2.0"
   exclude-result-prefixes="xalan j">

   <xsl:param name="config"/>
   
   <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" xalan:indent-amount="4" standalone="no"/>
   <xsl:strip-space elements="*"/>

   <xsl:template match="node()[name(.)='extensions']">
      <xsl:copy>
         <xsl:apply-templates select="node()|@*"/>
         <extension module="org.picketlink.as.extension"/>
      </xsl:copy>
   </xsl:template>

   <xsl:template match="node()[name(.)='profile']">
      <xsl:copy>
         <xsl:apply-templates select="@*|node()"/>
         <subsystem xmlns="urn:jboss:picketlink:1.0">
            <identity-management jndi-name="picketlink/ExampleIMF" alias="default">
                <jpa-store data-source="jboss/datasources/ExampleDS" realms="default">
                    <features supportsAll="true"/>
                </jpa-store>
            </identity-management>
        </subsystem>
      </xsl:copy>
   </xsl:template>

   <xsl:template match="@*|node()">
      <xsl:copy>
         <xsl:apply-templates select="@*|node()" />
      </xsl:copy>
   </xsl:template>

</xsl:stylesheet>
