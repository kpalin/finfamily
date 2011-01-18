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
	
	<xsl:choose>
	<xsl:when test="@style='NameIndexText'">
	<tr>
	
	<xsl:if test="not(descendant::b)">
	<td/>
	</xsl:if>
	<td class="NameIndexText">
	
	<xsl:apply-templates/>
	
	</td>
	
	<xsl:if test="descendant::b">
	<td/>
	</xsl:if>
	</tr>
	</xsl:when>
	<xsl:otherwise>
	 <xsl:variable name="style" select="@style"/>
  <div class="{$style}">
  <xsl:if test="@image">
	 <xsl:variable name="imgg" select="@image"/>
	 <xsl:variable name="imgw" select="@width"/>
	 <xsl:variable name="imgh" select="@height"/>
	<img src="{$imgg}" width="{$imgw}" height="{$imgh}" />
	<br/><xsl:value-of select="@title"/>
	</xsl:if>
  <xsl:apply-templates/>
  </div>
  </xsl:otherwise>
  </xsl:choose>
  </xsl:template>
  
  <xsl:template match="anchor">
   <xsl:variable name="aanch">
   <xsl:value-of select="."/>
   </xsl:variable>
  <a name="T{$aanch}"/>
  </xsl:template>
  
 
   <xsl:template match="n">
  
  <xsl:call-template name="dolink"/>
 
  </xsl:template> 
  
  
   <xsl:template match="u">
   <span class="ul">
 
  <xsl:call-template name="dolink"/>
  
  </span>
  </xsl:template> 
  
  <xsl:template match="bui">
  <strong class="ul"><i>
  <xsl:call-template name="dolink"/>
  </i></strong>
  </xsl:template> 
 
   <xsl:template match="b">
  
  <strong>
  
  <xsl:call-template name="dolink"/>
  </strong>
 
  </xsl:template> 
  
   <xsl:template name="dolink">
   <xsl:choose>
  <xsl:when test="@link">
    <xsl:variable name="lnknm" select="@link"/>
   <a href="#T{$lnknm}">
   <xsl:value-of select="."/>
  <!--  <xsl:apply-templates/> -->
  </a>
  </xsl:when>
  <xsl:otherwise>
   <xsl:apply-templates/>
  </xsl:otherwise>
</xsl:choose>
   </xsl:template>

  
  
   <xsl:template match="i">
  <i>
   <xsl:call-template name="dolink"/>
  </i>
  </xsl:template> 
  
  <xsl:template match="bi">
  <b><i>
 <xsl:call-template name="dolink"/>
  </i></b>
  </xsl:template> 
  
  <!--   this should be normal underline -->
  <xsl:template match="ui">
  <strong class="ul"><i>
   <xsl:call-template name="dolink"/>
  </i></strong>
  </xsl:template> 
  
  <xsl:template match="bu">
  <strong class="ul">
   <xsl:call-template name="dolink"/>
  </strong>
  </xsl:template> 
  
  
  <xsl:template match="nameIndex">
  <table>
   <xsl:apply-templates/>
   </table>
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