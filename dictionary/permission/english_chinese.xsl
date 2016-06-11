<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/dictionary">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>中文词汇 - Chinese Vocabularies</title>
</head>
<body>
<h1>中文词汇 - Chinese Vocabularies</h1>
<table border="1">
<xsl:for-each select="word">
<xsl:sort select="@id" />
<tr>
<!-- gif -->
<td>
<xsl:for-each select="url/gif">
<img width="10%">
<xsl:attribute name="src">
<xsl:value-of select="@src" />
</xsl:attribute>
</img>
</xsl:for-each>
</td>
<td>
<!-- character -->
<xsl:value-of select="chinese/character" />
</td>
<td>
<!-- pin yin -->
<xsl:value-of select="chinese/mandarin" />
</td>
<td>
<!-- english -->
<xsl:value-of select="english" />
</td>
</tr>
</xsl:for-each>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
