<xsl:stylesheet version="1.0" 
  xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
xmlns:aml="http://schemas.microsoft.com/aml/2001/core" xmlns:dt="uuid:C2F41010-65B3-11d1-A29F-00AA00C14882" xmlns:ve="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml" xmlns:wx="http://schemas.microsoft.com/office/word/2003/auxHint" xmlns:wsp="http://schemas.microsoft.com/office/word/2003/wordml/sp2" xmlns:sl="http://schemas.microsoft.com/schemaLibrary/2003/core"
 w:macrosPresent="no" w:embeddedObjPresent="no" w:ocxPresent="no" xml:space="preserve">
 <!--
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml" 
xmlns:v="urn:schemas-microsoft-com:vml" 
xmlns:o="urn:schemas-microsoft-com:office:office" 
xmlns:wsp="http://schemas.microsoft.com/office/word/2003/wordml/sp2"
xmlns:v="urn:schemas-microsoft-com:vml"

exclude-result-prefixes="v o">
-->

<xsl:template match="finfamily">
  <xsl:processing-instruction name="mso-application">progid="Word.Document"</xsl:processing-instruction> 
  <w:wordDocument xmlns:aml="http://schemas.microsoft.com/aml/2001/core" xmlns:dt="uuid:C2F41010-65B3-11d1-A29F-00AA00C14882" 
  xmlns:ve= "http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:o="urn:schemas-microsoft-com:office:office" 
  xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w10="urn:schemas-microsoft-com:office:word" 
  xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml" xmlns:wx="http://schemas.microsoft.com/office/word/2003/auxHint" 
  xmlns:wsp="http://schemas.microsoft.com/office/word/2003/wordml/sp2" xmlns:sl="http://schemas.microsoft.com/schemaLibrary/2003/core" 
  w:macrosPresent="no" w:embeddedObjPresent="no" w:ocxPresent="no" xml:space="preserve">
  <w:ignoreSubtree w:val="http://schemas.microsoft.com/office/word/2003/wordml/sp2" /> 
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
	 <xsl:variable name="stylename" select="@style"/>
	 <w:p wsp:rsidR="00295F9B" wsp:rsidRDefault="00A01824" wsp:rsidP="00A01824"><w:pPr>
	 <w:pStyle w:val="{$stylename}"/></w:pPr>
	 <xsl:apply-templates/>
	 </w:p>
  </xsl:template>
  
  
  
   <xsl:template match="n">
    <w:r><w:t><xsl:value-of select="."/></w:t></w:r>
  </xsl:template>
  
     <xsl:template match="b">
      <w:r wsp:rsidRPr="00A01824"><w:rPr><w:b/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r>
  </xsl:template>
  
     <xsl:template match="bu">
    <w:r wsp:rsidRPr="00A01824"><w:rPr><w:b/><w:u w:val="single"/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r>
  </xsl:template>
  
     <xsl:template match="u">
    <w:r wsp:rsidRPr="00A01824"><w:rPr><w:u w:val="single"/></w:rPr><w:t><xsl:value-of select="."/></w:t></w:r>
  </xsl:template>
  
  <xsl:include href="wstyles.xsl" /> 
  </xsl:stylesheet>