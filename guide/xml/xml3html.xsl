<xsl:stylesheet version = '1.0' 
     xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
>

<xsl:output method="html"/>

<xsl:template match="opas">

<xsl:apply-templates/>

</xsl:template>

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


<!--   Index page    -->
<xsl:template match="lista">



<html>
<head>

<title>
<xsl:value-of select="$tittel"/></title>

<link href="html/opas.css" rel="stylesheet" type="text/css"/>
</head>
<body class="list">
<table width="100%"><tr><td width="100"><a href="http://www.genealogia.fi"><IMG alt="Sukuohjelmisto" border="0" src="images/sttlogoh.gif"/></a></td>
<td valign="bottom"><h1><xsl:value-of select="$tittel"/></h1></td><td><a href="http://www.kk-software.fi"><IMG alt="KK-Software" border="0" src="images/KK-Software.gif"/></a></td></tr></table>
  <hr/>
<table><tr><td width="200"/><td>  
<table  border="0" >
<tr><td colspan="3">
<h2><xsl:value-of select="$index"/></h2></td></tr>

<xsl:apply-templates/>
</table>
</td></tr></table>
</body>
</html>
   
</xsl:template>

<xsl:template match="pre">
<pre>
<xsl:apply-templates/>
</pre>
</xsl:template>


<xsl:template match="itm">

<xsl:variable name="lhref" >

<xsl:choose>
<xsl:when test="@file">
<xsl:value-of select="@file"/>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="@href" />
</xsl:otherwise>
</xsl:choose>

</xsl:variable>
<tr>
<xsl:choose>
<xsl:when test="@level">
<tr><td width="50"></td><td width="50" class="bullet">&#x006C;</td><td class="list" width="500"><a href="html/{$lhref}.{$xfmt}"><xsl:value-of select="."/></a></td></tr>
</xsl:when>
<xsl:otherwise><td  width="50" class="bullet">&#x006E;</td>
<td class="list" colspan="2"><a href="html/{$lhref}.{$xfmt}"><xsl:value-of select="."/></a></td>
</xsl:otherwise>
</xsl:choose>

</tr>
<xsl:if test="@href">
 <xsl:variable name="docn">xml/<xsl:value-of select="$lhref"/>.xml</xsl:variable> 

  <xsl:document href="html/{$lhref}.html" method="html">
  <html>
  <head>
<title><xsl:value-of select="@title"/><xsl:value-of select="."/></title>
<link href="opas.css" rel="stylesheet" type="text/css"/>
</head>
  <body class="main">
  <xsl:variable name="preceurl"><xsl:value-of select="preceding-sibling::itm[position()=1]/@href"/></xsl:variable>
  
  <xsl:variable name="nxturl"><xsl:value-of select="following-sibling::itm[position()=1]/@href"/></xsl:variable>
  
  <table width="100%"><tr><td width="100"><a href="http://www.genealogia.fi"><IMG alt="Sukuohjelmisto" border="0" src="../images/sttlogoh.gif"/></a></td>
<td><h1><xsl:value-of select="$tittel"/></h1></td><td><a href="http://www.kk-software.fi"><IMG alt="KK-Software" border="0" src="../images/KK-Software.gif"/></a></td></tr></table>
  
  <table ><tr><td width="300">
  <xsl:if test="$preceurl!=''">
  <a href="{$preceurl}.html"><xsl:value-of select="$previous"/></a>( <xsl:value-of select="preceding-sibling::itm[position()=1]"/>)</xsl:if></td><td width="300"><a href="../index.html"><xsl:value-of select="$index"/></a></td>
  <td width="200">
  <xsl:if test="$nxturl!=''">
  <a href="{$nxturl}.html"><xsl:value-of select="$next"/></a> ( <xsl:value-of select="following-sibling::itm[position()=1]"/> ) </xsl:if></td></tr></table>
  <hr/>
  <h2>
  
  <xsl:value-of select="."/></h2>
 <xsl:apply-templates select="document(concat( @href, '.xml'))"/>
  </body>
  </html>
   </xsl:document>
</xsl:if>
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