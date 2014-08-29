<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:saxon="http://saxon.sf.net/" xmlns:functx="http://www.functx.com" version="3.0"
    extension-element-prefixes="saxon" exclude-result-prefixes="xs xd saxon functx">
    <!--   xmlns:err="http://www.w3.org/2005/xqt-errors" >-->

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>Templates for procesing Wikipedia pages and grouping</xd:p>
            <xd:p>Version 2.0</xd:p>
            <xd:p><xd:b>Revision:</xd:b> Jun 2013</xd:p>
            <xd:p><xd:b>Editor:</xd:b> Eliza Margaretha</xd:p>
						
            <xd:p><xd:b>Version 1.0 last modified:</xd:b> Jul 23, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> Stefanie Haupt</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:include href="Templates2.xsl"/>

    <xsl:output doctype-public="-//IDS//DTD IDS-XCES 1.0//EN"
        doctype-system="dtd/i5.dtd" method="xml"
        encoding="UTF-8" indent="yes"/>
    

    <xsl:param name="type" required="yes"/>
    <xsl:param name="korpusSigle" required="yes"/>
    <xsl:param name="lang" required="yes"/>
    <xsl:param name="origfilename" required="yes"/>
    <xsl:param name="pubDay" required="yes"/>
    <xsl:param name="pubMonth" required="yes"/>
    <xsl:param name="pubYear" required="yes"/>
    <xsl:param name="letter" required="yes"/>
    
    <xsl:variable name="errorCounter" select="0" saxon:assignable="yes"/>

    <xsl:param name="headerNames">
        <name>h1</name>
        <name>h2</name>
        <name>h3</name>
        <name>h4</name>
        <name>h5</name>
        <name>h6</name>
        <name>h7</name>
        <name>h8</name>
        <name>h9</name>
    </xsl:param>

    <xsl:template name="main">
        <xsl:variable name="doc">
            <xsl:copy-of saxon:read-once="yes" select="saxon:discard-document(.)"/>
        </xsl:variable>        
        <xsl:apply-templates  select="saxon:stream($doc/page)"/>
        
    </xsl:template>
    
    <xsl:template match="page">
        <!--Current index-->       

        <xsl:variable name="textSigle">
            <xsl:if test="string-length(id) gt 7">
                <xsl:message terminate="yes">ID länger als 7. Es kann kein gültiges textSigle
                    erzeugt werden. Abbruch.</xsl:message>
            </xsl:if>
            <xsl:variable name="intermediate">
                <xsl:sequence
                    select="concat($korpusSigle,'/',
                    upper-case($letter), 
                    if (string-length(id) lt 7)
                    then(concat(string-join(for $i in 1 to (7-string-length(id)) return '0', ''),string(id)))
                    else(string(id))
                    )"
                />
            </xsl:variable>
            <xsl:sequence
                select="concat(substring($intermediate,1,9),'.',substring($intermediate,10))"/>
        </xsl:variable>

        <xsl:variable name="t.title">
            <!--why is it a sequence when the values is not even a sequence? value-of is enough-->
            <xsl:sequence select="concat($textSigle,': ')"/>
            <xsl:value-of select="title"/>
            <xsl:sequence
                select="concat(', In: Wikipedia - URL:http://', $lang ,'.wikipedia.org/wiki/')"/>
            <!-- Assume this construct may be used as a weblink, ensure working link. -->
            <xsl:value-of select="translate(title,' ','_')"/>
            <xsl:sequence select="concat(': Wikipedia, ', $pubYear)"/>
        </xsl:variable>
        <!-- * idsText * -->
        <idsText>
            <xsl:attribute name="id" select="translate($textSigle,'/','.')"/>
            <!-- Avoid spaces in attribute 'n'. Attribute 'n' carries the value of the interwiki link -->
            <xsl:attribute name="n"
                select="concat(revision/text/@lang,concat('.',translate(title,' ','_')))"/>
            <xsl:attribute name="version" select="1.0"/>

            <!-- * idsHeader * -->
            <idsHeader type="text" pattern="text" status="new" version="1.0" TEIform="teiHeader">
                <fileDesc>
                    <titleStmt>
                        <textSigle>
                            <xsl:sequence select="$textSigle"/>
                        </textSigle>
                        <t.title assemblage="external">
                            <xsl:sequence select="$t.title"/>
                        </t.title>
                    </titleStmt>
                    <editionStmt version="0"/>
                    <publicationStmt>
                        <distributor/>
                        <pubAddress/>
                        <availability region="world">CC-BY-SA</availability>
                        <pubDate/>
                    </publicationStmt>
                    <sourceDesc Default="n">
                        <biblStruct Default="n">
                            <analytic>
                                <h.title type="main">
                                    <xsl:value-of select="title"/>
                                </h.title>
                                <h.title type="sub"/>
                                <h.title type="abbr" level="m"/>
                                <h.title type="abbr" level="a"/>
                                <h.author>
                                    <xsl:value-of select="revision/contributor/(username|ip)"/>
                                    <!-- Since there is only the ip or username of the last edit made to see, add 'u.a.' -->
                                    <xsl:text>,  u.a.</xsl:text>
                                </h.author>
                                <editor/>
                                <imprint/>
                                <biblScope type="subsume"/>
                                <biblScope type="pp"/>
                                <biblNote n="1"/>
                            </analytic>
                            <monogr>
                                <h.title type="main"/>
                                <editor>wikipedia.org</editor>
                                <edition>
                                    <further> Dump file &#34;<xsl:value-of select="$origfilename"
                                        />&#34; retrieved from http://dumps.wikimedia.org </further>
                                    <kind/>
                                    <appearance/>
                                </edition>
                                <imprint>
                                    <pubDate type="year">
                                        <xsl:sequence select="$pubYear"/>
                                    </pubDate>
                                    <pubDate type="month">
                                        <xsl:sequence select="$pubMonth"/>
                                    </pubDate>
                                    <pubDate type="day">
                                        <xsl:sequence select="$pubDay"/>
                                    </pubDate>
                                </imprint>
                                <biblScope type="vol"/>
                                <biblScope type="volume-title"/>
                            </monogr>
                        </biblStruct>
                        <reference type="complete" assemblage="non-automatic">
                            <xsl:sequence select="$t.title"/>
                        </reference>
                    </sourceDesc>
                </fileDesc>
                <encodingDesc>
                    <samplingDecl Default="n"/>
                    <editorialDecl Default="n">
                        <pagination type="no"/>
                    </editorialDecl>                    
                </encodingDesc>
                <profileDesc>
                    <creation>
                        <creatDate>
                            <xsl:sequence
                                select="format-dateTime(revision/timestamp, '[Y0001].[M01].[D01]')"
                            />
                        </creatDate>
                        <creatRef/>
                        <creatRefShort/>
                    </creation>
                    <textDesc>
                        <xsl:choose>
                            <xsl:when test="$type eq 'articles'">
                                <textTypeArt>Enzyklopädie-Artikel</textTypeArt>
                            </xsl:when>                            
                            <xsl:otherwise>
                                <textTypeArt>Diskussion</textTypeArt>        
                            </xsl:otherwise>
                        </xsl:choose>                        
                        <textDomain/>
                    </textDesc>
                </profileDesc>
            </idsHeader>

            <!-- * text * -->
            <text>
                <!-- front and back always empty -->
                <front/>
                <body>
                    <xsl:apply-templates select="revision"/>
                </body>
                <back/>
            </text>
        </idsText>
    </xsl:template>

    <xsl:template match="revision">        
        <xsl:try>
            <xsl:apply-templates select="text"/>
            <xsl:catch>
                <xsl:message>
                    <xsl:copy-of select="../title"/>
                    <xsl:text>error
                    </xsl:text>                    
                </xsl:message>
                <saxon:assign name="errorCounter" select="$errorCounter+1"/>
            </xsl:catch>
        </xsl:try>
    </xsl:template>

    <xsl:template match="text">
        <xsl:if test="parent::node()[name()='revision']">
            <!-- Start of the first section, level 0 -->
            <div n="0" type="section">
                <xsl:call-template name="section">
                    <xsl:with-param name="input" select="*"/>
                </xsl:call-template>
            </div>    
        </xsl:if>        
    </xsl:template>

    <xsl:template name="section">
        <xsl:param name="input"/>
        <!-- Group input by header level -->        
        <xsl:choose>            
            <xsl:when test="$input[name() eq 'h1']">                
                <xsl:for-each-group select="$input" group-starting-with="h1">                    
                    <xsl:call-template name="group">                        
                        <xsl:with-param name="groupingKey" select="'h1'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:when test="$input[name() eq 'h2']">                             
                <xsl:for-each-group select="$input" group-starting-with="h2">                    
                    <xsl:call-template name="group">
                        <xsl:with-param name="groupingKey" select="'h2'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:when test="$input[name() eq 'h3']">                
                <xsl:for-each-group select="$input" group-starting-with="h3">
                    <xsl:call-template name="group">
                        <xsl:with-param name="groupingKey" select="'h3'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:when test="$input[name() eq 'h4']">
                <xsl:for-each-group select="$input" group-starting-with="h4">
                    <xsl:call-template name="group">
                        <xsl:with-param name="groupingKey" select="'h4'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:when test="$input[name() eq 'h5']">
                <xsl:for-each-group select="$input" group-starting-with="h5">
                    <xsl:call-template name="group">
                        <xsl:with-param name="groupingKey" select="'h5'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:when test="$input[name() eq 'h6']">
                <xsl:for-each-group select="$input" group-starting-with="h6">
                    <xsl:call-template name="group">
                        <xsl:with-param name="groupingKey" select="'h6'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:when test="$input[name() eq 'h7']">
                <xsl:for-each-group select="$input" group-starting-with="h7">
                    <xsl:call-template name="group">
                        <xsl:with-param name="groupingKey" select="'h7'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:when test="$input[name() eq 'h8']">
                <xsl:for-each-group select="$input" group-starting-with="h8">
                    <xsl:call-template name="group">
                        <xsl:with-param name="groupingKey" select="'h8'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:when test="$input[name() eq 'h9']">
                <xsl:for-each-group select="$input" group-starting-with="h9">
                    <xsl:call-template name="group">
                        <xsl:with-param name="groupingKey" select="'h9'"/>
                    </xsl:call-template>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:otherwise>                 
                <xsl:call-template name="paragraphLevel">
                    <xsl:with-param name="input" select="$input"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="group">        
        <xsl:param name="groupingKey"/>            
        <xsl:choose>
            <!-- Group starting with header element -->
            <xsl:when test="name() eq $groupingKey">                   
                <xsl:apply-templates select="."/>
            </xsl:when>
            <!-- Group containing header element -->
            <xsl:when test="current-group()[name()=$headerNames/name]">                             
                <xsl:call-template name="section">
                    <xsl:with-param name="input" select="current-group()"/>
                </xsl:call-template>
            </xsl:when>
            <!-- Group without header element -->
            <xsl:otherwise>               
                <xsl:call-template name="paragraphLevel">
                    <xsl:with-param name="input" select="current-group()"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="paragraphLevel">
        <xsl:param name="input"/>        
        <xsl:for-each select="$input">
            <xsl:if test="text()[normalize-space(.)] | *">
                <xsl:choose>                    
                    <!-- Handling header inside invalid elements : Kompliciert discussion D/5372030.xml-->
                    <xsl:when test=".[not(name()=('dl','ul','ol')) and descendant::*[name()=$headerNames/name]]">
                        <xsl:call-template name="section">
                            <xsl:with-param name="input" select="*"/>
                        </xsl:call-template>                        
                    </xsl:when>                    
                    <!-- Handling non-paragraph elements--> <!-- also var-->
                    <xsl:when test="name()=('text','table','span','a','i','b','strong','u',
                        'small','big','sup','sub','tt','font','syntaxhighlight','Syntaxhighlight')">
                        <p>
                            <xsl:apply-templates select="."/>
                        </p>
                    </xsl:when>
                    <!-- Paragraph elements: paragraph, list, poem, quote, etc. -->
                    <xsl:otherwise>
                        <xsl:apply-templates select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:variable name="headingType">
        <xsl:choose>
            <xsl:when test="$type eq 'articles'">section</xsl:when>
            <xsl:otherwise>thread</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:template match="h1|h2|h3|h4|h5|h6|h7|h8">        
        <xsl:choose>            
            <xsl:when test="parent::node()[name() = ('dd','li','dt','ul','ol','dl')]">
                <xsl:if test="parent::node()[1]=.">
                    <xsl:call-template name="heading"/>
                </xsl:if>                                
            </xsl:when>
            <xsl:when test="parent::node()[name()='text']">
                <div n="{(substring(name(), 2))}" type="{$headingType}">
                    <xsl:call-template name="heading"/>                    
                    <!-- Continue processing elements in this header scope -->                                 
                    <xsl:call-template name="section">
                        <xsl:with-param name="input" select="current-group() except ."/>
                    </xsl:call-template>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <!-- Define header level and write header -->
                <div n="{(substring(name(), 2))}" type="{$headingType}">
                    <xsl:call-template name="heading"/>                    
                    <!-- Continue processing elements in this header scope -->                    
                    <xsl:call-template name="section">
                        <xsl:with-param name="input" select="current-group() except ."/>
                    </xsl:call-template>
                </div>        
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="heading">
        <head type="cross">
            <xsl:for-each select="child::node()">
                <xsl:choose>
                    <xsl:when test="name() eq 'br'"/>
                    <xsl:when test="name() eq 'p'">
                        <xsl:apply-templates/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </head>
    </xsl:template>

</xsl:stylesheet>
