<xsl:stylesheet 
      xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='2.0'
      xmlns:bar="http://apache.org/bar"
      exclude-result-prefixes="bar"
      >
      
 
      
  <xsl:param name="a-param">default param value</xsl:param>

  <xsl:output encoding="UTF-8"/>
  
  <xsl:template match="/">
    <xsl:comment><xsl:value-of select="system-property('xsl:product-version')"/></xsl:comment>
    <xsl:next-match/>
  </xsl:template>  
  
  <xsl:template match="finfamily">
  <kalle>
  <xsl:apply-templates/>
  </kalle>
  </xsl:template>
  <xsl:template match="header">
  <otsikko>
  Tässähän sitä
  </otsikko>
  </xsl:template>
  <xsl:template 
      match="@*|*|text()|processing-instruction()">
    <xsl:copy>
      <xsl:apply-templates 
         select="@*|*|text()|processing-instruction()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>