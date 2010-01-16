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
	
<xsl:output method="xml" encoding="UTF-8"/>  


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
<xsl:template match="i"><w:r wsp:rsidRPr="00A01824"><w:rPr><w:i/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r></xsl:template>
  

 
 
<xsl:template name="styles">
   <w:styles>
        <w:versionOfBuiltInStylenames w:val = "7"/>
        <w:latentStyles w:defLockedState = "off" w:latentStyleCount = "267">
            <w:lsdException w:name = "Normal"/>
            <w:lsdException w:name = "heading 1"/>
            <w:lsdException w:name = "heading 2"/>
            <w:lsdException w:name = "heading 3"/>
            <w:lsdException w:name = "heading 4"/>
            <w:lsdException w:name = "heading 5"/>
            <w:lsdException w:name = "heading 6"/>
            <w:lsdException w:name = "heading 7"/>
            <w:lsdException w:name = "heading 8"/>
            <w:lsdException w:name = "heading 9"/>
            <w:lsdException w:name = "caption"/>
            <w:lsdException w:name = "Title"/>
            <w:lsdException w:name = "Subtitle"/>
            <w:lsdException w:name = "Strong"/>
            <w:lsdException w:name = "Emphasis"/>
            <w:lsdException w:name = "Placeholder Text"/>
            <w:lsdException w:name = "No Spacing"/>
            <w:lsdException w:name = "Light Shading"/>
            <w:lsdException w:name = "Light List"/>
            <w:lsdException w:name = "Light Grid"/>
            <w:lsdException w:name = "Medium Shading 1"/>
            <w:lsdException w:name = "Medium Shading 2"/>
            <w:lsdException w:name = "Medium List 1"/>
            <w:lsdException w:name = "Medium List 2"/>
            <w:lsdException w:name = "Medium Grid 1"/>
            <w:lsdException w:name = "Medium Grid 2"/>
            <w:lsdException w:name = "Medium Grid 3"/>
            <w:lsdException w:name = "Dark List"/>
            <w:lsdException w:name = "Colorful Shading"/>
            <w:lsdException w:name = "Colorful List"/>
            <w:lsdException w:name = "Colorful Grid"/>
            <w:lsdException w:name = "Light Shading Accent 1"/>
            <w:lsdException w:name = "Light List Accent 1"/>
            <w:lsdException w:name = "Light Grid Accent 1"/>
            <w:lsdException w:name = "Medium Shading 1 Accent 1"/>
            <w:lsdException w:name = "Medium Shading 2 Accent 1"/>
            <w:lsdException w:name = "Medium List 1 Accent 1"/>
            <w:lsdException w:name = "Revision"/>
            <w:lsdException w:name = "List Paragraph"/>
            <w:lsdException w:name = "Quote"/>
            <w:lsdException w:name = "Intense Quote"/>
            <w:lsdException w:name = "Medium List 2 Accent 1"/>
            <w:lsdException w:name = "Medium Grid 1 Accent 1"/>
            <w:lsdException w:name = "Medium Grid 2 Accent 1"/>
            <w:lsdException w:name = "Medium Grid 3 Accent 1"/>
            <w:lsdException w:name = "Dark List Accent 1"/>
            <w:lsdException w:name = "Colorful Shading Accent 1"/>
            <w:lsdException w:name = "Colorful List Accent 1"/>
            <w:lsdException w:name = "Colorful Grid Accent 1"/>
            <w:lsdException w:name = "Light Shading Accent 2"/>
            <w:lsdException w:name = "Light List Accent 2"/>
            <w:lsdException w:name = "Light Grid Accent 2"/>
            <w:lsdException w:name = "Medium Shading 1 Accent 2"/>
            <w:lsdException w:name = "Medium Shading 2 Accent 2"/>
            <w:lsdException w:name = "Medium List 1 Accent 2"/>
            <w:lsdException w:name = "Medium List 2 Accent 2"/>
            <w:lsdException w:name = "Medium Grid 1 Accent 2"/>
            <w:lsdException w:name = "Medium Grid 2 Accent 2"/>
            <w:lsdException w:name = "Medium Grid 3 Accent 2"/>
            <w:lsdException w:name = "Dark List Accent 2"/>
            <w:lsdException w:name = "Colorful Shading Accent 2"/>
            <w:lsdException w:name = "Colorful List Accent 2"/>
            <w:lsdException w:name = "Colorful Grid Accent 2"/>
            <w:lsdException w:name = "Light Shading Accent 3"/>
            <w:lsdException w:name = "Light List Accent 3"/>
            <w:lsdException w:name = "Light Grid Accent 3"/>
            <w:lsdException w:name = "Medium Shading 1 Accent 3"/>
            <w:lsdException w:name = "Medium Shading 2 Accent 3"/>
            <w:lsdException w:name = "Medium List 1 Accent 3"/>
            <w:lsdException w:name = "Medium List 2 Accent 3"/>
            <w:lsdException w:name = "Medium Grid 1 Accent 3"/>
            <w:lsdException w:name = "Medium Grid 2 Accent 3"/>
            <w:lsdException w:name = "Medium Grid 3 Accent 3"/>
            <w:lsdException w:name = "Dark List Accent 3"/>
            <w:lsdException w:name = "Colorful Shading Accent 3"/>
            <w:lsdException w:name = "Colorful List Accent 3"/>
            <w:lsdException w:name = "Colorful Grid Accent 3"/>
            <w:lsdException w:name = "Light Shading Accent 4"/>
            <w:lsdException w:name = "Light List Accent 4"/>
            <w:lsdException w:name = "Light Grid Accent 4"/>
            <w:lsdException w:name = "Medium Shading 1 Accent 4"/>
            <w:lsdException w:name = "Medium Shading 2 Accent 4"/>
            <w:lsdException w:name = "Medium List 1 Accent 4"/>
            <w:lsdException w:name = "Medium List 2 Accent 4"/>
            <w:lsdException w:name = "Medium Grid 1 Accent 4"/>
            <w:lsdException w:name = "Medium Grid 2 Accent 4"/>
            <w:lsdException w:name = "Medium Grid 3 Accent 4"/>
            <w:lsdException w:name = "Dark List Accent 4"/>
            <w:lsdException w:name = "Colorful Shading Accent 4"/>
            <w:lsdException w:name = "Colorful List Accent 4"/>
            <w:lsdException w:name = "Colorful Grid Accent 4"/>
            <w:lsdException w:name = "Light Shading Accent 5"/>
            <w:lsdException w:name = "Light List Accent 5"/>
            <w:lsdException w:name = "Light Grid Accent 5"/>
            <w:lsdException w:name = "Medium Shading 1 Accent 5"/>
            <w:lsdException w:name = "Medium Shading 2 Accent 5"/>
            <w:lsdException w:name = "Medium List 1 Accent 5"/>
            <w:lsdException w:name = "Medium List 2 Accent 5"/>
            <w:lsdException w:name = "Medium Grid 1 Accent 5"/>
            <w:lsdException w:name = "Medium Grid 2 Accent 5"/>
            <w:lsdException w:name = "Medium Grid 3 Accent 5"/>
            <w:lsdException w:name = "Dark List Accent 5"/>
            <w:lsdException w:name = "Colorful Shading Accent 5"/>
            <w:lsdException w:name = "Colorful List Accent 5"/>
            <w:lsdException w:name = "Colorful Grid Accent 5"/>
            <w:lsdException w:name = "Light Shading Accent 6"/>
            <w:lsdException w:name = "Light List Accent 6"/>
            <w:lsdException w:name = "Light Grid Accent 6"/>
            <w:lsdException w:name = "Medium Shading 1 Accent 6"/>
            <w:lsdException w:name = "Medium Shading 2 Accent 6"/>
            <w:lsdException w:name = "Medium List 1 Accent 6"/>
            <w:lsdException w:name = "Medium List 2 Accent 6"/>
            <w:lsdException w:name = "Medium Grid 1 Accent 6"/>
            <w:lsdException w:name = "Medium Grid 2 Accent 6"/>
            <w:lsdException w:name = "Medium Grid 3 Accent 6"/>
            <w:lsdException w:name = "Dark List Accent 6"/>
            <w:lsdException w:name = "Colorful Shading Accent 6"/>
            <w:lsdException w:name = "Colorful List Accent 6"/>
            <w:lsdException w:name = "Colorful Grid Accent 6"/>
            <w:lsdException w:name = "Subtle Emphasis"/>
            <w:lsdException w:name = "Intense Emphasis"/>
            <w:lsdException w:name = "Subtle Reference"/>
            <w:lsdException w:name = "Intense Reference"/>
            <w:lsdException w:name = "Book Title"/>
            <w:lsdException w:name = "Bibliography"/>
            <w:lsdException w:name = "TOC Heading"/>
        </w:latentStyles>
        <w:style
            w:type = "paragraph"
            w:default = "on"
            w:styleId = "Normal">
            <w:name w:val = "Normal"/>
            <w:rsid w:val = "00295F9B"/>
            <w:pPr>
                <w:spacing
                    w:after = "200"
                    w:line = "276"
                    w:line-rule = "auto"/>
            </w:pPr>
            <w:rPr>
                <wx:font wx:val = "Calibri"/>
                <w:sz w:val = "22"/>
                <w:sz-cs w:val = "22"/>
                <w:lang
                    w:val = "FI"
                    w:fareast = "EN-US"
                    w:bidi = "AR-SA"/>
            </w:rPr>
        </w:style>
        <w:style
            w:type = "character"
            w:default = "on"
            w:styleId = "DefaultParagraphFont">
            <w:name w:val = "Default Paragraph Font"/>
        </w:style>
        <w:style
            w:type = "table"
            w:default = "on"
            w:styleId = "TableNormal">
            <w:name w:val = "Normal Table"/>
            <wx:uiName wx:val = "Table Normal"/>
            <w:rPr>
                <wx:font wx:val = "Calibri"/>
                <w:lang
                    w:val = "FI"
                    w:fareast = "FI"
                    w:bidi = "AR-SA"/>
            </w:rPr>
            <w:tblPr>
                <w:tblInd w:w = "0" w:type = "dxa"/>
                <w:tblCellMar>
                    <w:top w:w = "0" w:type = "dxa"/>
                    <w:left w:w = "108" w:type = "dxa"/>
                    <w:bottom w:w = "0" w:type = "dxa"/>
                    <w:right w:w = "108" w:type = "dxa"/>
                </w:tblCellMar>
            </w:tblPr>
        </w:style>
        <w:style
            w:type = "list"
            w:default = "on"
            w:styleId = "NoList">
            <w:name w:val = "No List"/>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "BodyText">
            <w:name w:val = "BodyText"/>
            <w:basedOn w:val = "Normal"/>
            <w:rsid w:val = "0072221F"/>
            <w:pPr>
                <w:spacing
                    w:after = "120"
                    w:line = "240"
                    w:line-rule = "auto"/>
            </w:pPr>
            <w:rPr>
                <w:rFonts w:ascii = "Times New Roman" w:h-ansi = "Times New Roman"/>
                <wx:font wx:val = "Times New Roman"/>
            </w:rPr>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "TableHeaderText">
            <w:name w:val = "TableHeaderText"/>
            <w:basedOn w:val = "BodyText"/>
            <w:rsid w:val = "006E337B"/>
            <w:pPr>
                <w:spacing w:before = "240" w:after = "0"/>
                <w:jc w:val = "center"/>
            </w:pPr>
            <w:rPr>
                <wx:font wx:val = "Times New Roman"/>
                <w:b/>
                <w:sz w:val = "28"/>
            </w:rPr>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "TableSubHeaderText">
            <w:name w:val = "TableSubHeaderText"/>
            <w:basedOn w:val = "BodyText"/>
            <w:rsid w:val = "000E6FBF"/>
            <w:pPr>
                <w:spacing w:after = "240"/>
                <w:jc w:val = "center"/>
            </w:pPr>
            <w:rPr>
                <wx:font wx:val = "Times New Roman"/>
                <w:sz w:val = "24"/>
            </w:rPr>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "ChildHeaderText">
            <w:name w:val = "ChildHeaderText"/>
            <w:basedOn w:val = "BodyText"/>
            <w:rsid w:val = "006E337B"/>
            <w:pPr>
                <w:spacing w:before = "120" w:after = "0"/>
                <w:ind w:left = "567"/>
            </w:pPr>
            <w:rPr>
                <wx:font wx:val = "Times New Roman"/>
            </w:rPr>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "ChildListText">
            <w:name w:val = "ChildListText"/>
            <w:basedOn w:val = "BodyText"/>
            <w:rsid w:val = "006E337B"/>
            <w:pPr>
                <w:ind w:left = "567"/>
            </w:pPr>
            <w:rPr>
                <wx:font wx:val = "Times New Roman"/>
            </w:rPr>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "MainPersonText">
            <w:name w:val = "MainPersonText"/>
            <w:basedOn w:val = "BodyText"/>
            <w:rsid w:val = "006E337B"/>
            <w:rPr>
                <wx:font wx:val = "Times New Roman"/>
            </w:rPr>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "ImageText">
            <w:name w:val = "ImageText"/>
            <w:basedOn w:val = "BodyText"/>
            <w:next w:val = "BodyText"/>
            <w:rsid w:val = "0036725D"/>
            <w:pPr>
                <w:jc w:val = "center"/>
            </w:pPr>
            <w:rPr>
                <wx:font wx:val = "Times New Roman"/>
            </w:rPr>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "SubPersonText">
            <w:name w:val = "SubPersonText"/>
            <w:basedOn w:val = "BodyText"/>
            <w:rsid w:val = "006E337B"/>
            <w:pPr>
                <w:spacing w:after = "60"/>
                <w:ind w:left = "1134"/>
            </w:pPr>
            <w:rPr>
                <wx:font wx:val = "Times New Roman"/>
                <w:sz w:val = "18"/>
            </w:rPr>
        </w:style>
        <w:style w:type = "paragraph" w:styleId = "Caption">
            <w:name w:val = "caption"/>
            <wx:uiName wx:val = "Caption"/>
            <w:basedOn w:val = "Normal"/>
            <w:next w:val = "Normal"/>
            <w:rsid w:val = "0036725D"/>
            <w:rPr>
                <wx:font wx:val = "Calibri"/>
                <w:b/>
                <w:b-cs/>
                <w:sz w:val = "20"/>
                <w:sz-cs w:val = "20"/>
            </w:rPr>
        </w:style>
    </w:styles>
  
</xsl:template>
  
 <!--font template --> 
<xsl:template name="fonts">
      <w:fonts>
        <w:defaultFonts
            w:ascii = "Calibri"
            w:fareast = "Calibri"
            w:h-ansi = "Calibri"
            w:cs = "Times New Roman"/>
        <w:font w:name = "Times New Roman">
            <w:panose-1 w:val = "02020603050405020304"/>
            <w:charset w:val = "00"/>
            <w:family w:val = "Roman"/>
            <w:pitch w:val = "variable"/>
            <w:sig
                w:usb-0 = "E0002AEF"
                w:usb-1 = "C0007841"
                w:usb-2 = "00000009"
                w:usb-3 = "00000000"
                w:csb-0 = "000001FF"
                w:csb-1 = "00000000"/>
        </w:font>
        <w:font w:name = "Cambria Math">
            <w:panose-1 w:val = "02040503050406030204"/>
            <w:charset w:val = "01"/>
            <w:family w:val = "Roman"/>
            <w:notTrueType/>
            <w:pitch w:val = "variable"/>
            <w:sig
                w:usb-0 = "00000000"
                w:usb-1 = "00000000"
                w:usb-2 = "00000000"
                w:usb-3 = "00000000"
                w:csb-0 = "00000000"
                w:csb-1 = "00000000"/>
        </w:font>
        <w:font w:name = "Calibri">
            <w:panose-1 w:val = "020F0502020204030204"/>
            <w:charset w:val = "00"/>
            <w:family w:val = "Swiss"/>
            <w:pitch w:val = "variable"/>
            <w:sig
                w:usb-0 = "A00002EF"
                w:usb-1 = "4000207B"
                w:usb-2 = "00000000"
                w:usb-3 = "00000000"
                w:csb-0 = "0000009F"
                w:csb-1 = "00000000"/>
        </w:font>
    </w:fonts>
</xsl:template>
 
 
 
 
 <!--Documentg properties-->
  <xsl:template name="doc-properties">
  
  <w:ignoreSubtree w:val = "http://schemas.microsoft.com/office/word/2003/wordml/sp2"/>
    <o:DocumentProperties>
        <o:Title>Table xxx</o:Title>
        <o:Author>Kalle</o:Author>
        <o:LastAuthor>Kalle</o:LastAuthor>
        <o:Revision>4</o:Revision>
        <o:TotalTime>6</o:TotalTime>
        <o:Created>2009-10-27T22:18:00Z</o:Created>
        <o:LastSaved>2009-10-28T07:52:00Z</o:LastSaved>
        <o:Pages>1</o:Pages>
        <o:Words>20</o:Words>
        <o:Characters>166</o:Characters>
        <o:Lines>1</o:Lines>
        <o:Paragraphs>1</o:Paragraphs>
        <o:CharactersWithSpaces>185</o:CharactersWithSpaces>
        <o:Version>12</o:Version>
    </o:DocumentProperties>
</xsl:template>
  
  
  <!--Printer settings -->
  
<xsl:template name="print-settings">
 
    <w:shapeDefaults>
        <o:shapedefaults v:ext = "edit" spidmax = "7170"/>
        <o:shapelayout v:ext = "edit">
            <o:idmap v:ext = "edit" data = "1"/>
        </o:shapelayout>
    </w:shapeDefaults>
    <w:docPr>
        <w:view w:val = "print"/>
        <w:zoom w:percent = "100"/>
        <w:doNotEmbedSystemFonts/>
        <w:defaultTabStop w:val = "1304"/>
        <w:hyphenationZone w:val = "425"/>
        <w:punctuationKerning/>
        <w:characterSpacingControl w:val = "DontCompress"/>
        <w:optimizeForBrowser/>
        <w:validateAgainstSchema/>
        <w:saveInvalidXML w:val = "off"/>
        <w:ignoreMixedContent w:val = "off"/>
        <w:alwaysShowPlaceholderText w:val = "off"/>
        <w:compat>
            <w:breakWrappedTables/>
            <w:snapToGridInCell/>
            <w:wrapTextWithPunct/>
            <w:useAsianBreakRules/>
            <w:dontGrowAutofit/>
        </w:compat>
        <wsp:rsids>
            <wsp:rsidRoot wsp:val = "00F3228B"/>
            <wsp:rsid wsp:val = "000E6FBF"/>
            <wsp:rsid wsp:val = "001F6888"/>
            <wsp:rsid wsp:val = "002011A7"/>
            <wsp:rsid wsp:val = "002633E6"/>
            <wsp:rsid wsp:val = "00295F9B"/>
            <wsp:rsid wsp:val = "00297271"/>
            <wsp:rsid wsp:val = "0032387E"/>
            <wsp:rsid wsp:val = "0036725D"/>
            <wsp:rsid wsp:val = "003A6F9F"/>
            <wsp:rsid wsp:val = "00445A00"/>
            <wsp:rsid wsp:val = "00483389"/>
            <wsp:rsid wsp:val = "00647006"/>
            <wsp:rsid wsp:val = "006E337B"/>
            <wsp:rsid wsp:val = "0072221F"/>
            <wsp:rsid wsp:val = "00A01824"/>
            <wsp:rsid wsp:val = "00CE692B"/>
            <wsp:rsid wsp:val = "00DF11C2"/>
            <wsp:rsid wsp:val = "00E34F87"/>
            <wsp:rsid wsp:val = "00F3228B"/>
        </wsp:rsids>
    </w:docPr>   
    
</xsl:template>

</xsl:stylesheet>