'use strict';

/**
 * Constructs a <code>XmlParser</code>. This has been included for backwards compatibility.
 *
 * <p>It is recommended that the {@link module:br/util/XmlParser#getInstance}
 * method is used to get an instance instead of
 * generating a new <code>XmlParser</code> every time.</p>
 * @module br/util/XmlParser
 */

/**
 * @alias module:br/util/XmlParser
 *
 * @classdesc
 * Utility class that provides methods for parsing XML strings into Document Objects. The object is a singleton so
 *  br.util.XmlParser should be used to obtain an instance of an <code>XmlParser</code>.
 *
 * <p>Example:</p>
 * <pre>
 * var oXmlParser = br.util.XmlParser;
 * var oDocument = oXmlParser.parse("&lt;test /&gt;");
 * alert(oDocument.tagName);
 * </pre>
 *
 * <p>The {@link module:br/util/XmlUtility} singleton provides further helper methods to manipulate XML.</p>
 */
function XmlParser() {
}

var COMMENT_START = /<!--/;
var COMMENT_END = /-->/;

/**
 * Parses an XML string, stripping out any whitespace and comments, and returns a document object representation.
 *
 * <p>This method supersedes {@link #parseString} as it will return an XML DOM that is consistent across different
 *  browsers. All comments and unnecessary whitespace characters will be stripped out of the XML DOM.</p>
 *
 * <p>Any encoded characters within the specified string (such as <code>&amp;</code>) will be decoded in the returned
 *  <code>DOMDocument</code>. Please see {@link module:br/util/XmlUtility} for more information on character encoding in XML.
 *  </p>
 *
 * @param {String} xml The XML string to be parsed.
 * @return {DOMDocument} An XML DOM representing the specified XML. If the XML failed to be parsed an XML DOM with the
 *  root node <code>parsererror</code> will be returned.
 */
XmlParser.parse = function(xml) {
	return this._parse(this.stripWhitespace(this._stripComments(xml)));
};

/**
 * Strips out all of the unnecessary whitespace characters from the specified XML string. These are the whitespace
 *  characters stripped at the beginning and end of the string, and in between each of the tags. This makes parsing of
 *  the XML easier in Firefox which includes the whitespace within the DOM tree.
 *
 * @param {String} xml The XML to have the whitespace stripped from.
 * @return {String} The XML with all unnecessary whitespace characters stripped out.
 * @see #parse
 */
XmlParser.stripWhitespace = function(xml) {
	var stripped = xml.trim();
	stripped = stripped.replace(/>\s*([^<]*)\s*</g, ">$1<");
	return stripped;
};

/**
 * Private method for parsing XML used by both the {@link #parse} and {@link #parseString} methods.
 *
 * @private
 * @param {String} xml The XML string to be parsed.
 * @return {DOMDocument} An XML DOM representing the specified XML.
 */
XmlParser._parse = function(xml) {
	var dom;

	try {
		//IE9 (native mode) & other browsers
		dom = (new DOMParser()).parseFromString(xml, 'text/xml');
		if (typeof dom.documentElement.selectSingleNode !== 'function' && !dom.evaluate) {
			throw new Error('Browser doesn\'t support XPath natively.');
		}
	} catch (e) {
		//IE9 (compatibility mode), IE8 & below
		dom = new ActiveXObject('Microsoft.XMLDOM');
		dom.async = 'false';
		dom.loadXML(xml);

		if (dom.documentElement === null) {
			var error = dom.parseError;
			dom.loadXML('<parsererror>XML parsing error ' + error.errorCode + ' at line: ' + error.line + ':' +
				error.linePos + '</parsererror>'
			);
		}
	}

	// IE (9?) will contain a null document element if the XML string is blank, which is converse to what other
	//  browsers do (they return an empty document element).
	if (dom.documentElement === null) {
		var oEmptyDoc = document.implementation.createDocument('', 'doc', null);
		return oEmptyDoc.documentElement;
	}

	return dom.documentElement;
};

/**
 * Strips all comments from within the specified XML string.
 * @private
 * @param {String} xml The XML to have the comments stripped from.
 * @return {String} The XML with all comments stripped out.
 */
XmlParser._stripComments = function(xml) {
	var stripped = xml;
	var commentStartMatch = null;
	var commentEndMatch = null;

	while ((commentStartMatch = stripped.match(COMMENT_START)) !== null) {
		commentEndMatch = stripped.match(COMMENT_END);
		if (commentEndMatch !== null) {
			stripped = stripped.substring(0, commentStartMatch.index) + stripped.substring(commentEndMatch.index + 3);
		} else {
			// a start comment has been found without a corresponding end
			break;
		}
	}

	return stripped;
};

module.exports = XmlParser;
