<xsl:stylesheet version="1.0" 
  xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
     xmlns:aml = "http://schemas.microsoft.com/aml/2001/core"
    xmlns:dt = "uuid:C2F41010-65B3-11d1-A29F-00AA00C14882"
    xmlns:ve = "http://schemas.openxmlformats.org/markup-compatibility/2006"
    xmlns:o = "urn:schemas-microsoft-com:office:office"
    xmlns:v = "urn:schemas-microsoft-com:vml"
    xmlns:w10 = "urn:schemas-microsoft-com:office:word"
    xmlns:w = "http://schemas.microsoft.com/office/word/2003/wordml"
    xmlns:wx = "http://schemas.microsoft.com/office/word/2003/auxHint"
    xmlns:wsp = "http://schemas.microsoft.com/office/word/2003/wordml/sp2"
    xmlns:sl = "http://schemas.microsoft.com/schemaLibrary/2003/core"
	>



<xsl:template match="finfamily">
  <xsl:processing-instruction name="mso-application">progid="Word.Document"</xsl:processing-instruction> 
  <w:wordDocument
    xmlns:aml = "http://schemas.microsoft.com/aml/2001/core"
    xmlns:dt = "uuid:C2F41010-65B3-11d1-A29F-00AA00C14882"
    xmlns:ve = "http://schemas.openxmlformats.org/markup-compatibility/2006"
    xmlns:o = "urn:schemas-microsoft-com:office:office"
    xmlns:v = "urn:schemas-microsoft-com:vml"
    xmlns:w10 = "urn:schemas-microsoft-com:office:word"
    xmlns:w = "http://schemas.microsoft.com/office/word/2003/wordml"
    xmlns:wx = "http://schemas.microsoft.com/office/word/2003/auxHint"
    xmlns:wsp = "http://schemas.microsoft.com/office/word/2003/wordml/sp2"
    xmlns:sl = "http://schemas.microsoft.com/schemaLibrary/2003/core"
    w:macrosPresent = "no"
    w:embeddedObjPresent = "no"
    w:ocxPresent = "no"
    xml:space = "preserve">
	

  <xsl:call-template name="doc-properties" /> 
  <xsl:call-template name="fonts" /> 
  <xsl:call-template name="styles" /> 
  <xsl:call-template name="print-settings" /> 
  <w:body>
  <xsl:apply-templates/> 
  <w:sectPr wsp:rsidR="00A01824" wsp:rsidRPr="0072221F" wsp:rsidSect="00295F9B">
  <w:pgSz w:w="11906" w:h="16838" /> 
  <w:pgMar w:top="1417" w:right="1134" w:bottom="1417" w:left="1134" w:header="708" w:footer="708" w:gutter="0" /> 
  <w:cols w:space="708" /> 
  <w:docGrid w:line-pitch="360" /> 
  </w:sectPr>
  </w:body>
  </w:wordDocument>
  </xsl:template>
   <xsl:template match="header">
  </xsl:template>
  
  
  <xsl:template match="body">
  <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="chapter">
	
	 <xsl:choose>
	 <xsl:when test="@image">
	 <xsl:call-template name="image"/>
	 </xsl:when>
	 <xsl:otherwise>
	  <xsl:variable name="stylename" select="@style"/>
	 <w:p wsp:rsidR="00295F9B" wsp:rsidRDefault="00A01824" wsp:rsidP="00A01824"><w:pPr>
	 <w:pStyle w:val="{$stylename}"/></w:pPr>
	  <xsl:apply-templates/>
	 </w:p>
	 </xsl:otherwise>
	 </xsl:choose>
	
  </xsl:template>
  
  <xsl:template name="image">
   <xsl:variable name="imgWidth" select="@width"/>
   <xsl:variable name="imgHeight" select="@height"/>
    
  <xsl:variable name="imgName" select="@image"/>
  <xsl:variable name="theImage" select="media"/>
   <xsl:variable name="stylename" select="@style"/>
  <w:p wsp:rsidR="00295F9B" wsp:rsidRDefault="00A01824" wsp:rsidP="00A01824"><w:pPr>
	 <w:pStyle w:val="{$stylename}"/></w:pPr>
	 
	<w:r>
		<w:pict>
			<v:shapetype
				id = "_x0000_t75"
				coordsize = "21600,21600"
				o:spt = "75"
				o:preferrelative = "t"
				path = "m@4@5l@4@11@9@11@9@5xe"
				filled = "f"
				stroked = "f">
				<v:stroke joinstyle = "miter"/>
				<v:formulas>
					<v:f eqn = "if lineDrawn pixelLineWidth 0"/>
					<v:f eqn = "sum @0 1 0"/>
					<v:f eqn = "sum 0 0 @1"/>
					<v:f eqn = "prod @2 1 2"/>
					<v:f eqn = "prod @3 21600 pixelWidth"/>
					<v:f eqn = "prod @3 21600 pixelHeight"/>
					<v:f eqn = "sum @0 0 1"/>
					<v:f eqn = "prod @6 1 2"/>
					<v:f eqn = "prod @7 21600 pixelWidth"/>
					<v:f eqn = "sum @8 21600 0"/>
					<v:f eqn = "prod @7 21600 pixelHeight"/>
					<v:f eqn = "sum @10 21600 0"/>
				</v:formulas>
				<v:path
					o:extrusionok = "f"
					gradientshapeok = "t"
					o:connecttype = "rect"/>
				<o:lock v:ext = "edit" aspectratio = "t"/>
			</v:shapetype>
			<w:binData w:name = "wordml://{$imgName}.jpg" xml:space = "preserve"><xsl:value-of select="$theImage"/></w:binData>
			<v:shape
				id = "_x0000_i1025"
				type = "#_x0000_t75"
				style = "width:{$imgWidth}pt;height:{$imgHeight}pt">
				<v:imagedata src = "wordml://{$imgName}.jpg" o:title = "kalleVIII"/>
			</v:shape>
		</w:pict>
	</w:r>
 
 </w:p>
 <xsl:if test="@title">
	  <w:p
		wsp:rsidR = "00297271"
		wsp:rsidRDefault = "0036725D"
		wsp:rsidP = "0036725D">
		
		<w:pPr>
			<w:pStyle w:val = "Caption"/>
			<w:jc w:val = "center"/>
		</w:pPr>
		<xsl:if test="@imageNo">
			<w:r>
				<w:t><xsl:value-of select="@imageName"/><xsl:text> </xsl:text></w:t>
			</w:r>
			
			<w:fldSimple w:instr = " SEQ Kuva \* ARABIC ">
				<w:r>
					<w:rPr>
						<w:noProof/>
					</w:rPr>
					<w:t><xsl:value-of select="@imageNo"/></w:t>
				</w:r>
			</w:fldSimple>
		</xsl:if>
		<w:r>
			<w:t><xsl:text> </xsl:text><xsl:value-of select="@title"/></w:t>
		</w:r>
	</w:p>
</xsl:if>
</xsl:template>
  

  
<xsl:template match="n"><w:r><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
<xsl:template match="b"><w:r wsp:rsidRPr="00A01824"><w:rPr><w:b/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
<xsl:template match="bu"><w:r wsp:rsidRPr="00A01824"><w:rPr><w:b/><w:u w:val="single"/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
<xsl:template match="u"><w:r wsp:rsidRPr="00A01824"><w:rPr><w:u w:val="single"/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
<xsl:template match="bui"><w:r wsp:rsidRPr="00A01824"><w:rPr><w:b/><w:i/><w:u w:val="single"/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
<xsl:template match="bi"><w:r wsp:rsidRPr="00A01824"><w:rPr><w:b/><w:i/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
<xsl:template match="ui"><w:r wsp:rsidRPr="00A01824"><w:rPr><w:i/><w:u w:val="single"/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
<xsl:template match="i"><w:r wsp:rsidRPr="00A01824"><w:rPr><w:i/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
  
  
  <xsl:include href="styles2003.xsl" /> 
  </xsl:stylesheet>