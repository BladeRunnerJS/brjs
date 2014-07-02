/**
 * Constructs a <code>XmlParser</code>. This has been included for backwards compatibility.
 * 
 * <p>It is recommended that the {@link br.util.XmlParser#getInstance} method is used to get an instance instead of
 * generating a new <code>XmlParser</code> every time.</p>
 * @constructor
 * 
 * @class
 * Utility class that provides methods for parsing XML strings into Document Objects. The
 * object is a singleton so br.util.XmlParser should be used to obtain an instance of an
 * <code>XmlParser</code>.
 * 
 * <p>Example:</p>
 * <pre>
 * var oXmlParser = br.util.XmlParser;
 * var oDocument = oXmlParser.parse("&lt;test /&gt;");
 * alert(oDocument.tagName);
 * </pre>
 * 
 * <p>The {@link br.util.XmlUtility} singleton provides further helper methods to manipulate
 * XML.</p>
 */
br.util.XmlParser = function() {};

/** @private */
br.util.XmlParser.COMMENT_START = /<!--/;
/** @private */
br.util.XmlParser.COMMENT_END = /-->/;

/**
 * Parses an XML string, stripping out any whitespace and comments, and returns a document object
 * representation.
 * 
 * <p>This method supersedes {@link #parseString} as it will return an XML DOM that is consistent
 * across different browsers. All comments and unnecessary whitespace characters will be stripped
 * out of the XML DOM.</p>
 * 
 * <p>Any encoded characters within the specified string (such as <code>&amp;</code>) will be
 * decoded in the returned <code>DOMDocument</code>. Please see {@link br.util.XmlUtility} for
 * more information on character encoding in XML.</p>
 * 
 * @param {String} sXml The XML string to be parsed.
 * @type DOMDocument
 * @return An XML DOM representing the specified XML. If the XML failed to be parsed an XML DOM
 *		 with the root node <code>parsererror</code> will be returned.
 */
br.util.XmlParser.parse = function(sXml)
{
	return this._parse(this.stripWhitespace(this._stripComments(sXml)));
};

/**
 * Strips out all of the unnecessary whitespace characters from the specified XML string. These are
 * the whitespace characters stripped at the beginning and end of the string, and in between each of
 * the tags. This makes parsing of the XML easier in Firefox which includes the whitespace within
 * the DOM tree.
 * 
 * @param {String} sXml The XML to have the whitespace stripped from.
 * @type String
 * @return The XML with all unnecessary whitespace characters stripped out.
 * @see #parse 
 */
br.util.XmlParser.stripWhitespace = function(sXml)
{
	var sStripped = (sXml.trim());
	sStripped = sStripped.replace(/>\s*([^<]*)\s*</g, ">$1<");
	return sStripped;
};

/**
 * Private method for parsing XML used by both the {@link #parse} and {@link #parseString} methods.
 * 
 * @param {String} sXml The XML string to be parsed.
 * @type DOMDocument
 * @return An XML DOM representing the specified XML.
 * @private
 */
br.util.XmlParser._parse = function(sXml)
{
	var oDom;
	try
	{
		//IE9 (native mode) & other browsers
		oDom = (new DOMParser()).parseFromString(sXml, "text/xml");
		if(typeof oDom.documentElement.selectSingleNode !== "function" && !oDom.evaluate)
		{
			throw new Error("Browser doesn't support XPath natively.");
		}
	}
	catch (e)
	{
		//IE9 (compatibility mode), IE8 & below
		oDom = new ActiveXObject("Microsoft.XMLDOM");
		oDom.async = "false";
		oDom.loadXML(sXml);

		if (oDom.documentElement === null)
		{
			var error = oDom.parseError;
			oDom.loadXML("<parsererror>XML parsing error " + error.errorCode + " at line: " + error.line + ":" + error.linePos + "</parsererror>");
		}
	}
	
	// IE (9?) will contain a null document element if the XML string is blank,
	// which is converse to what other browsers do (they return an empty document element). 
	if (oDom.documentElement === null)
	{
		var oEmptyDoc = document.implementation.createDocument("", "doc", null);
		return oEmptyDoc.documentElement;
	}
	
	return oDom.documentElement;
};

/**
 * Strips all comments from within the specified XML string. 
 * @param {String} sXml The XML to have the comments stripped from.
 * @type String
 * @return The XML with all comments stripped out.
 * @private
 */
br.util.XmlParser._stripComments = function(sXml)
{
	var sStripped = sXml;
	var oCommentStartMatch = null;
	while ((oCommentStartMatch = sStripped.match(this.COMMENT_START)) !== null)
	{
		oCommentEndMatch = sStripped.match(this.COMMENT_END);
		if (oCommentEndMatch !== null)
		{
			sStripped = sStripped.substring(0, oCommentStartMatch.index) + sStripped.substring(oCommentEndMatch.index + 3);
		}
		else
		{
			// a start comment has been found without a corresponding end
			break;
		}
	}
	return sStripped;
};
