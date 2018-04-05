<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:siard="http://www.bar.admin.ch/xmlns/siard/2/metadata.xsd"
  xmlns:html="http://www.w3.org/1999/xhtml"
  version="2.0">
  
  <xsl:output method="html" encoding="utf-8" indent="yes" />

  <xsl:template match="/">
    <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
    <html>
      <xsl:apply-templates select="/siard:siardArchive"/>
    </html>
  </xsl:template>
  
  <xsl:template match="/siard:siardArchive">
    <head>
	    <title>SIARD Database Meta Data Summary</title>
      <meta charset="utf-8" />
		  <style>
		    dl { margin: 0; padding: 0; }
		    dt, dd { padding: 1mm; margin-top: 0; margin-bottom: 0; }
				dt /* puts dt and dd on same line and right-adjusts dt */
				{
			    float: left;
			    width: 15em;
			    text-align: right;
			  }
			  dt:after { content: ":"; }
			  dd:after /* handles empty values */
			  {
			    content: "&#160;"; /* nbsp */
			  }
			  /* extend header styles beyond h6 */
			  h1 { margin-left:  0mm; display: block; font-size: 2.0em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
			  h2 { margin-left:  5mm; display: block; font-size: 1.8em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
  	    h3 { margin-left: 10mm; display: block; font-size: 1.6em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
  	    h4 { margin-left: 15mm; display: block; font-size: 1.4em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
  	    h5 { margin-left: 20mm; display: block; font-size: 1.2em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
  	    h6 { margin-left: 25mm; display: block; font-size: 1.0em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
  	    h7 { margin-left: 30mm; display: block; font-size: 0.9em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
  	    h8 { margin-left: 35mm; display: block; font-size: 0.8em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
  	    h9 { margin-left: 40mm; display: block; font-size: 0.7em; font-weight: bold; margin-top: 0; margin-bottom: 0; }
		  </style>
    </head>
    <body>
      <h1>SIARD Database Meta Data Summary</h1>
      <dl>
        <dt>SIARD Format</dt>
          <dd><xsl:value-of select="@version"/></dd>
        <dt>Database name</dt>
          <dd><xsl:value-of select="siard:dbname"/></dd>
        <dt>Description</dt>
          <dd><xsl:value-of select="siard:description"/></dd>
        <dt>Archiver</dt>
          <dd><xsl:value-of select="siard:archiver"/></dd>
        <dt>Archiver contact</dt>
          <dd><xsl:value-of select="siard:archiverContact"/></dd>
        <dt>Data owner</dt>
          <dd><xsl:value-of select="siard:dataOwner"/></dd>
        <dt>Data origin timespan</dt>
          <dd><xsl:value-of select="siard:dataOriginTimespan"/></dd>
        <dt>Producer application</dt>
          <dd><xsl:value-of select="siard:producerApplication"/></dd>
        <dt>Archival date</dt>
          <dd><xsl:value-of select="siard:archivalDate"/></dd>
        <dt>Message digest</dt>
	        <dd>
	          <xsl:for-each select="siard:messageDigest">
	            <xsl:value-of select="siard:digestType"/>:<xsl:value-of select="siard:digest"/>
	            <xsl:if test="position()!=last()"><br/></xsl:if>
	          </xsl:for-each>
	        </dd>
        <dt>Archived from</dt>
          <dd><xsl:value-of select="siard:clientMachine"/></dd>
        <dt>Database product</dt>
          <dd><xsl:value-of select="siard:databaseProduct"/></dd>
        <dt>Connection</dt>
          <dd><xsl:value-of select="siard:connection"/></dd>
        <dt>Database user</dt>
          <dd><xsl:value-of select="siard:databaseUser"/></dd>
      </dl>
      <h2>Schemas</h2>
      <xsl:apply-templates select="siard:schemas/siard:schema"/>
      <h2>Users</h2>
      <xsl:apply-templates select="siard:users/siard:user"/>
      <h2>Roles</h2>
      <xsl:apply-templates select="siard:roles/siard:role"/>
      <h2>Privileges</h2>
      <xsl:apply-templates select="siard:privileges/siard:privilege"/>
    </body>
  </xsl:template>
  
  <xsl:template match="siard:schemas/siard:schema">
    <h3>Schema <xsl:value-of select="siard:name"/></h3>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
    <h3>Types</h3>
    <xsl:apply-templates select="siard:types/siard:type"/>
    <h3>Tables</h3>
    <xsl:apply-templates select="siard:tables/siard:table"/>
    <h3>Views</h3>
    <xsl:apply-templates select="siard:views/siard:view"/>
    <h3>Routines</h3>
    <xsl:apply-templates select="siard:routines/siard:routine"/>
  </xsl:template>
  
  <xsl:template match="siard:types/siard:type">
    <h4>Type <xsl:value-of select="siard:name"/></h4>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Category</dt>
        <dd><xsl:value-of select="siard:category"/></dd>
      <xsl:choose>
        <xsl:when test="siard:category != 'distinct'">
		      <dt>Instantiable</dt>
            <dd><xsl:value-of select="siard:instantiable"/></dd>
		      <dt>Final</dt>
            <dd><xsl:value-of select="siard:final"/></dd>
        </xsl:when>
        <xsl:otherwise>
          <dt>Base</dt>
            <dd><xsl:value-of select="siard:base"/></dd>
        </xsl:otherwise>
      </xsl:choose>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
    <xsl:if test="siard:category = 'udt'">
      <h5>Attributes</h5>
      <xsl:apply-templates select="siard:attributes/siard:attribute"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="siard:attributes/siard:attribute">
    <h6>Attribute <xsl:value-of select="siard:name"/></h6>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <xsl:choose>
        <xsl:when test="siard:type">
          <dt>Data type</dt>
            <dd><xsl:value-of select="siard:type"/></dd>
        </xsl:when>
        <xsl:otherwise>
          <dt>Type schema</dt>
            <dd><xsl:value-of select="siard:typeSchema"/></dd>
          <dt>Type name</dt>
            <dd><xsl:value-of select="siard:typeName"/></dd>
        </xsl:otherwise>
      </xsl:choose>
      <dt>Original data type</dt>
        <dd><xsl:value-of select="siard:typeOriginal"/></dd>
      <dt>Nullable</dt>
        <dd><xsl:value-of select="siard:nullable"/></dd>
      <dt>Default value</dt>
        <dd><xsl:value-of select="siard:defaultValue"/></dd>
      <xsl:if test="siard:cardinality">
        <dt>Cardinality</dt>
          <dd><xsl:value-of select="siard:cardinality"/></dd>
      </xsl:if>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="siard:tables/siard:table">
    <h4>Table <xsl:value-of select="siard:name"/></h4>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Rows</dt>
        <dd><xsl:value-of select="siard:rows"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
    <h5>Columns</h5>
    <xsl:apply-templates select="siard:columns/siard:column"/>
    <xsl:if test="siard:primaryKey">
      <h5>Primary Key</h5>
      <xsl:apply-templates select="siard:primaryKey"/>
    </xsl:if>
    <xsl:if test="siard:candidateKeys">
      <h5>Candidate Keys</h5>
      <xsl:apply-templates select="siard:candidateKeys/siard:candidateKey"/>
    </xsl:if>
    <xsl:if test="siard:foreignKeys">
      <h5>Foreign Keys</h5>
      <xsl:apply-templates select="siard:foreignKeys/siard:foreignKey"/>
    </xsl:if>
    <xsl:if test="siard:triggers">
      <h5>Triggers</h5>
      <xsl:apply-templates select="siard:triggers/siard:trigger"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="siard:views/siard:view">
    <h4>View <xsl:value-of select="siard:name"/></h4>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Query</dt>
        <dd><xsl:value-of select="siard:query"/></dd>
      <dt>Original query</dt>
        <dd><xsl:value-of select="siard:queryOriginal"/></dd>
      <dt>Rows</dt>
        <dd><xsl:value-of select="siard:rows"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
    <h5>Columns</h5>
    <xsl:apply-templates select="siard:columns/siard:column"/>
  </xsl:template>
  
  <xsl:template match="siard:columns/siard:column">
    <h6>Column <xsl:value-of select="siard:name"/></h6>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <xsl:if test="siard:lobFolder">
        <dt>LOB folder</dt>
          <dd><xsl:value-of select="siard:lobFolder"/></dd>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="siard:type">
          <dt>Data type</dt>
            <dd><xsl:value-of select="siard:type"/></dd>
          <xsl:if test="siard:mimeType">
            <dt>MIME type</dt>
            <dd><xsl:value-of select="siard:mimeType"/></dd>
          </xsl:if>
        </xsl:when>
        <xsl:otherwise>
          <dt>Type schema</dt>
            <dd><xsl:value-of select="siard:typeSchema"/></dd>
          <dt>Type name</dt>
            <dd><xsl:value-of select="siard:typeName"/></dd>
        </xsl:otherwise>
      </xsl:choose>
      <dt>Original data type</dt>
        <dd><xsl:value-of select="siard:typeOriginal"/></dd>
      <dt>Nullable</dt>
        <dd><xsl:value-of select="siard:nullable"/></dd>
      <dt>Default value</dt>
        <dd><xsl:value-of select="siard:defaultValue"/></dd>
      <xsl:if test="siard:cardinality">
        <dt>Cardinality</dt>
          <dd><xsl:value-of select="siard:cardinality"/></dd>
      </xsl:if>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
    <xsl:if test="siard:fields">
      <h7>Fields</h7>
      <xsl:apply-templates select="siard:fields/siard:field"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="siard:fields/siard:field">
    <h8>Field <xsl:value-of select="siard:name"/></h8>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <xsl:if test="siard:lobFolder">
        <dt>LOB folder</dt>
          <dd><xsl:value-of select="siard:lobFolder"/></dd>
      </xsl:if>
      <xsl:if test="siard:mimeType">
        <dt>MIME type</dt>
        <dd><xsl:value-of select="siard:mimeType"/></dd>
      </xsl:if>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
    <xsl:if test="siard:fields">
      <h8>Subfields</h8>
      <xsl:apply-templates select="siard:fields/siard:field"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="siard:primaryKey">
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Columns</dt>
        <dd>
          <xsl:for-each select="siard:column">
            <xsl:value-of select="."/>
            <xsl:if test="position()!=last()"><br/></xsl:if>
          </xsl:for-each>
        </dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="siard:candidateKeys/siard:candidateKey">
    <h6>CandiateKey <xsl:value-of select="siard:name"/></h6>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Columns</dt>
        <dd>
          <xsl:for-each select="siard:column">
            <xsl:value-of select="."/>
            <xsl:if test="position()!=last()"><br/></xsl:if>
          </xsl:for-each>
        </dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="siard:foreignKeys/siard:foreignKey">
    <h6>Foreign Key <xsl:value-of select="siard:name"/></h6>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Referenced schema</dt>
        <dd><xsl:value-of select="siard:referencedSchema"/></dd>
      <dt>Referenced table</dt>
        <dd><xsl:value-of select="siard:referencedTable"/></dd>
      <dt>References</dt>
        <dd>
          <xsl:for-each select="siard:reference">
            <xsl:value-of select="siard:column"/>: <xsl:value-of select="siard:referenced"/>
            <xsl:if test="position()!=last()"><br/></xsl:if>
          </xsl:for-each>
        </dd>
      <dt>Match type</dt>
        <dd><xsl:value-of select="siard:matchType"/></dd>
      <dt>Delete action</dt>
        <dd><xsl:value-of select="siard:deleteActionAction"/></dd>
      <dt>Update action</dt>
        <dd><xsl:value-of select="siard:updateAction"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="siard:triggers/siard:trigger">
    <h6>Trigger <xsl:value-of select="siard:name"/></h6>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Action time</dt>
        <dd><xsl:value-of select="siard:actionTime"/></dd>
      <dt>Trigger event</dt>
        <dd><xsl:value-of select="siard:triggerEvent"/></dd>
      <xsl:if test="siard:aliasList">
        <dt>Aliases</dt>
        <xsl:for-each select="siard:aliasList">
          <xsl:value-of select="."/>
          <xsl:if test="position()!=last()"><br/></xsl:if>
        </xsl:for-each>
      </xsl:if>
      <dt>Triggered action</dt>
        <dd><xsl:value-of select="siard:triggeredAction"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="siard:routines/siard:routine">
    <h4>Routine <xsl:value-of select="siard:routine"/></h4>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
      <dt>Source</dt>
        <dd><xsl:value-of select="siard:source"/></dd>
      <dt>Body</dt>
        <dd><xsl:value-of select="siard:body"/></dd>
      <dt>Characteristic</dt>
        <dd><xsl:value-of select="siard:characteristic"/></dd>
      <dt>Return type</dt>
        <dd><xsl:value-of select="siard:returnType"/></dd>
    </dl>
     <h5>Parameters</h5>
     <xsl:apply-templates select="siard:parameters/siard:parameter"/>
  </xsl:template>
  
  <xsl:template match="siard:parameters/siard:parameter">
    <h6>Parameter <xsl:value-of select="siard:name"/></h6>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Mode</dt>
        <dd><xsl:value-of select="siard:mode"/></dd>
      <xsl:choose>
        <xsl:when test="siard:type">
		      <dt>Type</dt>
		        <dd><xsl:value-of select="siard:type"/></dd>
        </xsl:when>
        <xsl:otherwise>
          <dt>Type schema</dt>
            <dd><xsl:value-of select="siard:typeSchema"/></dd>
          <dt>Type name</dt>
            <dd><xsl:value-of select="siard:typeName"/></dd>
        </xsl:otherwise>
      </xsl:choose>
      <dt>Original type</dt>
        <dd><xsl:value-of select="siard:typeOriginal"/></dd>
      <xsl:if test="siard:cardinality">
        <dt>Cardinality</dt>
          <dd><xsl:value-of select="siard:cardinality"/></dd>
      </xsl:if>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="siard:users/siard:user">
    <h3>User <xsl:value-of select="siard:name"/></h3>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="siard:roles/siard:role">
    <h3>Role <xsl:value-of select="siard:name"/></h3>
    <dl>
      <dt>Name</dt>
        <dd><xsl:value-of select="siard:name"/></dd>
      <dt>Admin</dt>
        <dd><xsl:value-of select="siard:admin"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="siard:privileges/siard:privilege">
    <h3>Privilege</h3>
    <dl>
      <dt>Type</dt>
        <dd><xsl:value-of select="siard:type"/></dd>
      <dt>Object</dt>
        <dd><xsl:value-of select="siard:object"/></dd>
      <dt>Grantor</dt>
        <dd><xsl:value-of select="siard:grantor"/></dd>
      <dt>Grantee</dt>
        <dd><xsl:value-of select="siard:grantee"/></dd>
      <dt>Option</dt>
        <dd><xsl:value-of select="siard:option"/></dd>
      <dt>Description</dt>
        <dd><xsl:value-of select="siard:description"/></dd>
    </dl>
  </xsl:template>
  
</xsl:stylesheet>