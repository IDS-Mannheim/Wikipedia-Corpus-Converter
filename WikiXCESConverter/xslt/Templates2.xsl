<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:saxon="http://saxon.sf.net/" xmlns:functx="http://www.functx.com"
    exclude-result-prefixes="xs xd saxon functx" version="3.0">

    <xd:doc scope="stylesheet">
        <xd:desc>TES
            <xd:p>Templates for various elements</xd:p>
            <xd:p><xd:b>Date:</xd:b> June 2013</xd:p>
            <xd:p><xd:b>Author:</xd:b> Eliza Margaretha</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:output name="text" indent="yes" omit-xml-declaration="yes"/>
    
    <xsl:param name="inflectives" required="yes"/>
        
    <xsl:param name="phraseNames">
        <name>small</name>
        <name>big</name>
        <name>a</name>
        <name>u</name>
        <name>i</name>
        <name>b</name>
        <name>strong</name>
        <name>sub</name>
        <name>sup</name>
        <name>font</name>
        <name>tt</name>
        <name>em</name>
        <name>syntaxhighlight</name>
        <name>code</name>
        <name>source</name>
    </xsl:param>

    <xsl:param name="inflectiveNames">
        <xsl:copy-of select="doc($inflectives)//inflectives//name"/>
    </xsl:param>
    
    <xsl:param name="divClasses">
        <name>BoxenVerschmelzen</name>
        <name>NavFrame</name>
        <name>references-small</name>
        <name>tright</name>
        <name>sideBox</name>
    </xsl:param>
    
    
    <!-- Paragraph Level Templates -->

    <xsl:template match="p">
        <xsl:if test="text()[normalize-space(.)] | *">
            <xsl:choose>
                <!-- Handle paragraph inside phrase elements -->
                <xsl:when test="parent::node()[name()=$phraseNames/*]">                   
                    <xsl:choose>
                        <!-- When the phrase element contains header and is escaped -->
                        <xsl:when
                            test="preceding-sibling::node()/descendant-or-self::node()/name()=$headerNames/* or
                            following-sibling::node()/descendant-or-self::node()/name()=$headerNames/*">
                            <p>
                                <xsl:apply-templates/>
                            </p>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>                
                <xsl:otherwise>
                    <p>
                        <xsl:apply-templates/>
                    </p>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <xsl:template match="blockquote">
        <xsl:if test="text()[normalize-space(.)] | *">
            <xsl:choose>
                <xsl:when test="parent::node()/name() = ('p','blockquote','poem',$phraseNames/*)">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <quote>
                        <xsl:apply-templates/>                       
                    </quote>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <xsl:template match="poem | Poem">
        <xsl:if test="text()[normalize-space(.)] | *">
            <xsl:choose>
                <xsl:when test="parent::node()/name() = ('p','poem',$phraseNames/*)">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <poem>
                        <xsl:for-each select="child::node()">
                            <xsl:choose>
                                <xsl:when test="name()='p'">
                                    <l>
                                        <xsl:apply-templates/> 
                                    </l>
                                </xsl:when>
                                <xsl:otherwise>
                                    <l>
                                        <xsl:apply-templates select="."/>
                                    </l>        
                                </xsl:otherwise>
                            </xsl:choose>                            
                        </xsl:for-each>
                    </poem>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <xsl:template match="ul | ol | dl">
        <list type="{@type}">
            <xsl:attribute name="type" select="name()"/>
            <xsl:for-each select="child::node()">
                <xsl:if test="text()[normalize-space(.)] | *">
                    <xsl:choose>
                        <xsl:when test="name()=('li','dd','dt')">
                            <xsl:apply-templates select="."/>
                        </xsl:when>
                        <xsl:when test="name()=('p','poem','blockquote')">
                            <item>
                                <xsl:apply-templates/>
                            </item>
                        </xsl:when>
                        <xsl:when test="descendant::node()/name()=('li','dd','dt')">
                            <xsl:apply-templates
                                select="descendant::node()[name()=('li','dd','dt')]"/>
                        </xsl:when>
                        <xsl:otherwise/>
                        <!-- Other elements are skipped -->
                    </xsl:choose>
                </xsl:if>
            </xsl:for-each>
        </list>
    </xsl:template>

    <xsl:template match="li | dd | dt">
        <xsl:for-each select="child::node()">
            <xsl:choose>
                <xsl:when test="name()=$headerNames/*">
                    <xsl:apply-templates select="."/>
                </xsl:when>
                <xsl:when test="name()=('p','poem','blockquote')">
                    <item>
                        <xsl:apply-templates select="child::node()"/>
                    </item>
                </xsl:when>
                <xsl:when test="../../name()=('ul','ol','dl')">
                    <item>
                        <xsl:apply-templates select="."/>
                    </item>
                </xsl:when>
                <xsl:when test="../../name()=('dd','dt')">
                    <xsl:apply-templates select="."/>
                </xsl:when>
                <xsl:otherwise/>
                <!-- Incorrect placements of the element are skipped. -->
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="div">
        <xsl:if test="text()[normalize-space(.)] | *">
            <xsl:choose>
                <xsl:when test="@class='thumbcaption'">
                    <xsl:call-template name="caption"/>
                </xsl:when>
                <xsl:when test="@class=('tickerList','toccolours')">
                    <xsl:call-template name="gap">
                        <xsl:with-param name="name" select="@class"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="@class=$divClasses/*">
                    <xsl:variable name="value">
                        <div type="{@class}">
                            <xsl:call-template name="section">
                                <xsl:with-param name="input" select="*"/>
                            </xsl:call-template>
                        </div>
                    </xsl:variable>
                    <xsl:call-template name="div">                        
                        <xsl:with-param name="test1" select="$value"/>
                        <xsl:with-param name="test2" select="$value"/>   
                    </xsl:call-template>              
                </xsl:when>
                <xsl:when test="parent::node()/name()=('blockquote')">                    
                    <p><xsl:value-of select="."/></p>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="div">
                        <xsl:with-param name="test1">
                            <div type="other">
                                <p>
                                    <xsl:value-of select="."/>
                                </p>
                            </div>
                        </xsl:with-param>
                        <xsl:with-param name="test2">
                            <p>
                                <xsl:value-of select="."/>
                            </p>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <xsl:template name="div">
        <xsl:param name="test1"/>
        <xsl:param name="test2"/>
        <xsl:choose>
            <xsl:when test="parent::node()/name()=('text','blockquote','center','div')">
                <xsl:copy-of select="$test1"/>
            </xsl:when>
            <xsl:when test="preceding-sibling::node()/name()=$headerNames/* or
                following-sibling::node()/name()=$headerNames/*">
                <xsl:copy-of select="$test2"/>
            </xsl:when>
            <xsl:when test="parent::node()=('p',$phraseNames)">
                <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>    
    </xsl:template>

    <xsl:template match="caption">
        <xsl:call-template name="caption"/>
    </xsl:template>

    <xsl:template name="caption">
        <xsl:choose>
            <xsl:when test="parent::node()/name()='table'"/>
            <xsl:when test="parent::node()/name()=('text','td','div')">
                <caption>
                    <xsl:choose>
                        <xsl:when test="child::node()/name()!=('p','ul','ol','dl','poem','blockquote')">
                            <p>
                                <!-- Handling complex structures in a caption - TODO: hack version due to false conversion -->
                                <xsl:for-each select="child::node()">
                                    <xsl:choose>
                                        <xsl:when test="name()=('p','poem','div','center')">
                                            <xsl:value-of select="."/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:apply-templates select="."/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:for-each>                                
                            </p>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates/>
                        </xsl:otherwise>
                    </xsl:choose>
                </caption>
            </xsl:when>
            <xsl:when test="parent::node()[name()=$phraseNames/*]">
                <xsl:variable name="value"><xsl:value-of select="."/></xsl:variable>
                <xsl:call-template name="phrase">
                    <xsl:with-param name="value" select="$value"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="parent::node()/name()=('p','dd','dt','li')">
                    <xsl:value-of select="."/> </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>

    </xsl:template>

    <xsl:template name="phrase">
        <xsl:param name="value"/>
        <xsl:choose>
            <!-- When inside a phrase element containing a header-->
            <xsl:when
                test="preceding-sibling::node()/name()=$headerNames/* or
            following-sibling::node()/name()=$headerNames/*">
                <p>
                    <xsl:copy-of select="$value"/>
                </p>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="$value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="pre">
        <xsl:if test="text()[normalize-space(.)] | *">
            <xsl:variable name="value"> <xsl:value-of select="."/> </xsl:variable>

            <xsl:choose>
                <xsl:when test="parent::node()/name()=('text','td','th','div')">
                    <div type="{name()}">
                        <p>
                            <xsl:apply-templates/>
                        </p>
                    </div>
                </xsl:when>
                <xsl:when test="parent::node()/name()=('blockquote')">
                    <p>
                        <xsl:apply-templates/>
                    </p>
                </xsl:when>
                <xsl:when test="parent::node()[name()=$phraseNames/*]">
                    <xsl:call-template name="phrase">
                        <xsl:with-param name="value" select="$value"/>
                    </xsl:call-template>
                </xsl:when>
                <!-- parent node: p, li, dd, poem, blockquote -->
                <xsl:when test="parent::node()/name()=('p','li','dd')">
                    <xsl:copy-of select="$value"/>
                </xsl:when>
                <!-- Incorrect placement, e.g. inside an s or timeline -->
                <xsl:otherwise/>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <xsl:template match="center">
        <xsl:choose>
            <xsl:when test="parent::node()/name()=('text','center')">
                <div type="center">
                    <xsl:call-template name="section">
                        <xsl:with-param name="input" select="*"/>
                    </xsl:call-template>
                </div>
            </xsl:when>
            <xsl:when test="parent::node()[name()='div' and @class=($divClasses/*,'thumbcaption')]">
                <div type="center">
                    <xsl:call-template name="section">
                        <xsl:with-param name="input" select="*"/>
                    </xsl:call-template>
                </div>
            </xsl:when>
            <!-- When inside another element and a header is a sibling -->
            <xsl:when
                test="preceding-sibling::node()/name()=$headerNames/* or
                following-sibling::node()/name()=$headerNames/*">
                <div type="center">
                    <xsl:call-template name="section">
                        <xsl:with-param name="input" select="*"/>
                    </xsl:call-template>
                </div>
            </xsl:when>            
            <xsl:otherwise>                
                <xsl:value-of select="."/> </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- Posting Template -->
    <xsl:template match="posting">  
        <xsl:if test="text()[normalize-space(.)] | *">
        <xsl:element name="posting">
            <xsl:attribute name="indentLevel" select="@indentLevel"/>
            <xsl:if test="@who">
                <xsl:variable name="author" select="@who"/>
                <xsl:attribute name="who" select="$author"/>
            </xsl:if>
            <xsl:if test="@synch">
                <xsl:variable name="timestamp" select="@synch"/>
                <xsl:attribute name="synch" select="$timestamp"/>
            </xsl:if>            
            
            <xsl:for-each select="*">
                <xsl:choose>
                    <xsl:when test="name()=('p','gap')">
                        <p>
                            <xsl:apply-templates/>
                        </p>
                    </xsl:when>
                    <xsl:when test="name()=('div','poem','dl','ul','ol')">
                        <xsl:apply-templates select="."/>
                    </xsl:when>
                    <xsl:when test="name()=('pre','center','blockquote','strike','s')">
                        <p><xsl:value-of select="."/></p>
                    </xsl:when>                                        
                    <xsl:otherwise>
                        <p><xsl:apply-templates/></p>
                        <xsl:message>rest <xsl:copy-of select="."/>
                        </xsl:message>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>         
        </xsl:element>
<!--        </div>    -->
        </xsl:if>
    </xsl:template>


    <!-- Phrase Level Templates -->
    
    <xsl:template match="autoSignature">
        <autoSignature/>
    </xsl:template>
    
    <xsl:template match="seg">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="ref|Ref|REF">
        <xsl:if test="text()[normalize-space(.)] | *">
            <xsl:choose>
                <xsl:when test="parent::node()/name()='text'">
                    <p> <xsl:apply-templates/></p>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    

    <xsl:template match="table">
        <gap desc="table" reason="omitted"/>
    </xsl:template>

    <xsl:template match="tr|td|th"/>

    <xsl:template match="timeline | references | gallery | Gallery">
        <xsl:call-template name="gap">
            <xsl:with-param name="name" select="lower-case(name())"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="gap">
        <xsl:param name="name"/>        
        <xsl:choose>
            <xsl:when test="parent::node()/name()=('text','center','div','posting')">
                <p>
                    <gap desc="{$name}" reason="omitted"/>
                </p>
            </xsl:when>
            <xsl:when test="parent::node()[name()=$phraseNames/*]">
                <xsl:variable name="value">
                    <gap desc="{$name}" reason="omitted"/>
                </xsl:variable>
                <xsl:call-template name="phrase">
                    <xsl:with-param name="value" select="$value"/>
                </xsl:call-template>
            </xsl:when>
            <!--  blockquote invalid-->
            <xsl:when test="parent::node()[name()=('p', 'li', 'dd', 'dt')]">
                <gap desc="{$name}" reason="omitted"/>                
            </xsl:when>
            <xsl:otherwise/>                
        </xsl:choose>
    </xsl:template>

    <xsl:template match="span">
        <xsl:choose>
            <xsl:when test="@class eq 'tag-extension'">
                <gap desc="{@id}" reason="omitted"/>
            </xsl:when>
            <xsl:when test="@class eq 'template'">
                <gap desc="template" reason="omitted"/>
            </xsl:when>
            <xsl:when test="@class eq 'signature'">
                <xsl:message>signature</xsl:message>
            </xsl:when>
            <!--<xsl:when test="@class eq 'unknown-node'">                
                <gap desc="{@name}" reason="omitted"/>
            </xsl:when>  -->
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="abbr">
        <abbr>
            <xsl:value-of select="."/>
        </abbr>
    </xsl:template>

    <xsl:template match="br">
        <lb/>
    </xsl:template>

    <xsl:template match="a">
        <xsl:variable select="@href" name="url"/>
        <ref target="{$url}" targOrder="u">
            <xsl:value-of select="text()"/>
        </ref>
    </xsl:template>

    <xsl:template match="b | strong">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="bo">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <xsl:template match="i">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="it">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <xsl:template match="u">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="ul">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <xsl:template match="small | big">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="pt">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <xsl:template match="sup">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="super">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <xsl:template match="sub">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="sub">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <xsl:template match="tt">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="tt">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <xsl:template match="em | code | source">
        <xsl:call-template name="hi">
            <xsl:with-param name="rend" select="name()"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="hi">
        <xsl:param name="rend"/>
        <xsl:if test="text()[normalize-space(.)] | *">
            <xsl:choose>
                <xsl:when test="parent::node()/name()='text'">
                    <p>
                        <hi rend="{$rend}">
                            <xsl:choose>
                                <xsl:when test="child::node()/name()=('p','poem','dl','ul','ol')">
                                    <xsl:value-of select="."/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:apply-templates/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </hi>
                    </p>
                </xsl:when>
                <xsl:when test="parent::node()/name()=('p','dd','dt','li',$phraseNames)">
                    <hi rend="{$rend}">
                        <xsl:apply-templates/>
                    </hi>
                </xsl:when>
                <xsl:otherwise/>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <xsl:template match="font">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="font-style">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <xsl:template match="syntaxhighlight | Syntaxhighlight">
        <xsl:if test="text()[normalize-space(.)] | *">
            <hi rend="syntaxhighlight">
                <xsl:apply-templates/>
            </hi>
        </xsl:if>
    </xsl:template>

    <!-- Escaped Element Templates -->
    
    <xsl:template match="s | strike | del">        
        <xsl:call-template name="esc">
            <xsl:with-param name="name" select="name()"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="node()[name()=$inflectiveNames/*]">        
        <xsl:call-template name="esc">
            <xsl:with-param name="name" select="name()"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="*">
        <xsl:variable name="name">
            <xsl:choose>
                <xsl:when test="name() eq 'div'">
                    <xsl:value-of select="name()"/>-<xsl:choose>
                        <xsl:when test="not(empty(@class))">
                            <xsl:value-of select="@class"/>
                        </xsl:when>
                        <xsl:otherwise>other</xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="name()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:call-template name="esc">
            <xsl:with-param name="name" select="$name"/>
        </xsl:call-template>

    </xsl:template>

    <xsl:template name="esc">
        <xsl:param name="name"/>
        <xsl:if test="text()[normalize-space(.)] | *">
            <xsl:variable name="value"> <xsl:value-of select="."/>  </xsl:variable>
            <xsl:choose>        
                <xsl:when test="parent::node()[name()='div' and @class = ('tickerList','toccolours')]"/>
                <xsl:when test="parent::node()[name()='div' and @class = 'thumbcaption']">
                    <xsl:copy-of select="$value"/>
                </xsl:when>
                <xsl:when test="parent::node()[name()=('text','center','div')]">
                    <p>
                        <xsl:copy-of select="$value"/>
                    </p>
                </xsl:when>                
                <xsl:otherwise>
                    <xsl:copy-of select="$value"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
