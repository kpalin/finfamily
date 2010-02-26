<xsl:stylesheet version = '1.0' 
     xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
>

<xsl:output method="html"/>

<xsl:variable name="xfmt">html</xsl:variable>
<xsl:variable name="lang">fi</xsl:variable>
<xsl:variable name="tittel">FinFamily Release notes</xsl:variable>


<xsl:template match="p">
<p>
<xsl:apply-templates />
</p>
</xsl:template>

<xsl:template match="link">
<xsl:variable name="llref" select="@lref"/>
  <xsl:element name="a">
   <xsl:copy-of select="@target"/>
   <xsl:choose>
   <xsl:when test="@lref">
   <xsl:attribute name="href"><xsl:value-of select="@lref"/>.<xsl:value-of select="$xfmt"/></xsl:attribute>

   </xsl:when>
   <xsl:otherwise>
   <xsl:copy-of select="@href"/>   
   </xsl:otherwise>
   </xsl:choose>
   <xsl:choose>
    <xsl:when test=".!=''">
    <xsl:value-of select="."/>
    </xsl:when>
    <xsl:otherwise>
    <xsl:value-of select="@href"/>
    <xsl:value-of select="@lref"/>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:element>
</xsl:template>





<xsl:template match="hdr[@xml:lang=$lang or not(@xml:lang)]">
<h2><xsl:apply-templates/></h2>
</xsl:template>

<xsl:template match="hdr"/>

<xsl:template match="strong">
<strong><xsl:apply-templates/></strong>
</xsl:template>


<xsl:template match="pre">
<pre>
<xsl:apply-templates/>
</pre>
</xsl:template>


<xsl:template match="opas">

  <html>
  <head>
<title><xsl:value-of select="@title"/><xsl:value-of select="."/></title>

<style type="text/css">

.list {
	background-color: #ffffc0 ;
	font:  bold 14pt cursive;

}
.list H1 { font: 28pt cursive;
	font-weight:700 }

.bullet{background-color: #ffffc0 ;
	font: bold 14pt wingdings;
	color : Blue;
}
	

.nimi {
	background-color: #ccffff ;
	font: bold 14pt serif;
	color : Blue;
}

.header {background-color: #ccffff; }
.header H1 { font: 24pt cursive;
	font-weight:700 }

	
.main {background-color: #ffffc0;
	  font: 13pt serif }
	  
.main TD {font: 13pt serif }
	  
.main H1 { font: 28pt cursive;
	font-weight:700 }

.main H2 { font: 20pt cursive;
	font-weight:700 }
	
	
.main H3 { font: 14pt cursive;
	font-weight:700 }
	
.main H4 { font: 12pt cursive;
	font-weight:700 }
	
.list LI { margin-left: -2em;
    padding-left: 0em;
	text-indent: 0em;
	}
	
.ul {
	text-decoration : underline;
}
	

</style>
</head>
  <body class="main">
  <xsl:variable name="preceurl"><xsl:value-of select="preceding-sibling::itm[position()=1]/@href"/></xsl:variable>
  
  <xsl:variable name="nxturl"><xsl:value-of select="following-sibling::itm[position()=1]/@href"/></xsl:variable>
  
  <table width="100%"><tr><td width="100"><a href="http://www.genealogia.fi"><IMG alt="Sukuohjelmisto" border="0" src="http://www.sukuohjelmisto.fi/fi/images/sttlogoh.gif"/></a></td>
<td align="center"><h1><xsl:value-of select="$tittel"/></h1></td><td><a href="http://www.kk-software.fi"><IMG alt="KK-Software" border="0" src="http://www.sukuohjelmisto.fi/fi/images/KK-Software.gif"/></a></td></tr></table>
  
 <!-- <table ><tr><td width="100%">
  <xsl:if test="$preceurl!=''">
  <a href="{$preceurl}.html"><xsl:value-of select="$previous"/></a>( <xsl:value-of select="preceding-sibling::itm[position()=1]"/>)</xsl:if></td><td width="300"><a href="../index.html"><xsl:value-of select="$index"/></a></td>
  <td width="200">
  <xsl:if test="$nxturl!=''">
  <a href="{$nxturl}.html"><xsl:value-of select="$next"/></a> ( <xsl:value-of select="following-sibling::itm[position()=1]"/> ) </xsl:if></td></tr></table> -->
  <hr/>
  
  <xsl:apply-templates />
  </body>
  </html>
</xsl:template>




<xsl:template match="nimi">
<html>
<head>
<title><xsl:value-of select="@title"/></title>
<link href="opas.css" rel="stylesheet" type="text/css"/>
</head>
<body class="nimi">
<table width="100%" >
<tr><td width="220"><a href="http://www.kk-software.fi" target="_top"><img src="../images/KK-Software.gif" width="100" height="27" border="0" alt=""/></a></td>
<td align="center" class="nimi"><xsl:value-of select="@title"/></td>
<td width="220"></td></tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template match="uline">
<span class="ul"><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match="euro">
&#x20AC;
</xsl:template>



<xsl:template match="i|b|li|td|tr|ul">
<xsl:variable name="ib" select="name()"/>
<xsl:element name="{$ib}">
<xsl:apply-templates />
</xsl:element>
</xsl:template>


<xsl:template match="table">
<table>
<tbody>
<xsl:apply-templates />
</tbody>
</table>
</xsl:template>


<xsl:template match="image">
<xsl:variable name="lsrc1" select="@src" />
<xsl:variable name="lsrc2" select="concat('../images/',$lsrc1)"/>
<xsl:variable name="ltype" select="@type"/>


<xsl:choose>
<xsl:when test="@type='inline'">
<img src="{$lsrc2}" border="0"/>
</xsl:when>
<xsl:otherwise>

<div align="center">
<img src="{$lsrc2}" alt="@alt"/>
</div>
</xsl:otherwise>
</xsl:choose>

</xsl:template>

<xsl:template match="version[@xml:lang =$lang]">
<h2><xsl:value-of select="@number"/></h2>
<xsl:value-of select="$datetext"/><xsl:text> </xsl:text><xsl:value-of select="@date"/><br/>
<ol>
<xsl:apply-templates/>
</ol>
<hr/>
</xsl:template>

<xsl:template match="version"/>


</xsl:stylesheet>