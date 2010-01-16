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

body {
	font-size:10pt;
	font-family : 'Arial',sansserif;
}


.BodyText {
	
	font-weight: normal;
	margin-left: 6pt;
	margin-bottom:6pt;
	text-align:left
}

.ChildHeaderText {
	
	font-weight: normal;
	margin-left: 12pt;
	text-align:left
}

.ChildListText {
	
	font-weight: normal;
	margin-left: 18pt;
	margin-top:6pt;
	text-align:left
}

.ImageText{
	
	font-weight: normal;
	margin-left: 6pt;
	margin-bottom:6pt;
	text-align:center
}


.MainPersonText{
	
	font-weight: normal;
	margin-left: 6pt;
	margin-bottom:6pt;
	text-align:left
}

.SubPersonText{
	font-size:smaller;
	font-weight: normal;
	margin-left: 36pt;
	margin-bottom:3pt;
	text-align:left
}

.TableHeaderText{
	font-size:150%;
	font-weight: bold;
	margin-top:12pt;
	text-align:center
}

.TableSubHeaderText{
	font-size:120%;
	font-weight: normal;
	
	margin-bottom:12pt;
	text-align:center
}

.ul {  
	text-decoration:underline;
}
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
	<img src="{$imgg}" width="{$imgw}" height="{$imgh}" />
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
  
    <xsl:template match="bui">
  <strong class="ul"><i>
  <xsl:apply-templates/>
  </i></strong>
  </xsl:template> 
  
   <xsl:template match="b">
  <strong>
  <xsl:apply-templates/>
  </strong>
  </xsl:template> 
  
    <xsl:template match="i">
  <i>
  <xsl:apply-templates/>
  </i>
  </xsl:template> 
     <xsl:template match="bi">
  <b><i>
  <xsl:apply-templates/>
  </i></b>
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