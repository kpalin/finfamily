<xsl:stylesheet 
      xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='2.0'
      
      >
      
 <xsl:include href="finfamily.css.xsl" />
      
  <xsl:param name="a-param">default param value</xsl:param>

  <xsl:output method="html" encoding="UTF-8"/>
  
 
  
  <xsl:template match="finfamily">
  <html>
  <xsl:apply-templates/>
  </html>
  </xsl:template>
  
  <xsl:template match="header">
  <head>
  <title><xsl:value-of select="title"/></title>
  <!--
   <xsl:variable name="stylg" select="concat(/finfamily/header/@folder,'/finfamily.css')"/>
   <link href="{$stylg}" rel="stylesheet" type="text/css"/>
-->
  <STYLE type="text/css">
<xsl:value-of select="$CSS_CONTENT"/>
</STYLE>

  </head>
  </xsl:template>
  
  <xsl:template match="body">
  <body>
  <xsl:apply-templates/>
  </body>
  </xsl:template>
  
    <xsl:template match="chapter">
	
	
	 <xsl:variable name="style" select="@style"/>
  <div class="{$style}">
  <xsl:if test="@image">
	 <xsl:variable name="imgg" select="concat(/finfamily/header/@folder,'/',@image)"/>
	 <xsl:variable name="imgw" select="@width"/>
	 <xsl:variable name="imgh" select="@height"/>
	<img src="{$imgg}" width="{$imgw}" />
	<br/><xsl:value-of select="@title"/>
	</xsl:if>
  <xsl:apply-templates/>
  </div>
  </xsl:template>
  
  <xsl:template match="bu">
  <strong class="ul">
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