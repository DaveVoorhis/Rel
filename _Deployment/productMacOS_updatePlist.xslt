<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <!-- Identity transform -->
   <xsl:template match="@* | node()">
      <xsl:copy>
         <xsl:apply-templates select="@* | node()"/>
      </xsl:copy>
   </xsl:template>

   <!-- Insert Java VM invocation in appropriate place. -->
   <xsl:template match="string[text()='~/.eclipse_keyring']">
      <xsl:copy-of select="."/>
		<string>-vm</string><string>jre/bin/java</string>
   </xsl:template>
</xsl:stylesheet>
