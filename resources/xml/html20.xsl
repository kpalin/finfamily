<xsl:stylesheet 
      xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='2.0'
      xmlns:bar="http://apache.org/bar"
      exclude-result-prefixes="bar"
      >
      
 <!--
 XSLT 2.0 requires the saxon home library to be installed.
 It is much larger than rest of finfamily package will possibly not
 be as a part of finfamily. First version of XML report is thus done
 with standard java tools and XSLT 1.0
 -->
      
	  
	  
  <xsl:param name="a-param">default param value</xsl:param>

  <xsl:output method="xhtml" encoding="UTF-8"/>
  
  <xsl:template match="/">
    <xsl:comment><xsl:value-of select="system-property('xsl:product-version')"/></xsl:comment>
    <xsl:next-match/>
  </xsl:template>  
  
  <xsl:template match="finfamily">
  <html>
  <xsl:apply-templates/>
  </html>
  </xsl:template>
  
  <xsl:template match="header">
  <head>
  <title>Kallen koetta tässä</title>
  <STYLE type="text/css">
P#mypar {font-style: italic; color: blue}
#ul {text-decoration:underline}
</STYLE>

  </head>
  </xsl:template>
  
  <xsl:template match="body">
  <body>
  <xsl:apply-templates/>
  </body>
  </xsl:template>
  
    <xsl:template match="chapter">
  <p>
  <xsl:apply-templates/>
  </p>
  </xsl:template>
  
  <xsl:template match="bu">
  <strong id="ul">
  <xsl:apply-templates/>
  </strong>
  </xsl:template> 
  
   <xsl:template match="b">
  <strong>
  <xsl:apply-templates/>
  </strong>
  </xsl:template> 
  <!--
  <xsl:template 
      match="@*|*|text()|processing-instruction()">
    <xsl:copy>
      <xsl:apply-templates 
         select="@*|*|text()|processing-instruction()"/>
    </xsl:copy>
  </xsl:template>
  -->
</xsl:stylesheet>