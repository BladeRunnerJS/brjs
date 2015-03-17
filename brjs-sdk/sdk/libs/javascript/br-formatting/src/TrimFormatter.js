/**
 * @module br/formatting/TrimFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var RegExpFormatter = require('br/formatting/RegExpFormatter');

/**
 * @class
 * @alias module:br/formatting/TrimFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Trims whitespace from boths ends of the string.
 */
function TrimFormatter() {
	this.regExpFormatter = new RegExpFormatter();
}

topiarist.implement(TrimFormatter, Formatter);

/**
 * Trims whitespace from boths ends of the string.
 * <p/>
 * <code>TrimFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "Buy USD":
 * <p/>
 * <code>br.formatting.TrimFormatter.format(" Buy USD ", {})</code>
 *
 * @param {Variant} vValue  the string to trim.
 * @param {Map} mAttributes  the map of attributes.
 * @return  the trimmed string.
 * @type  String
 */
TrimFormatter.prototype.format = function(vValue, mAttributes) {
	var mRegExpAttributes = {
		match: "(^(\\s|\\u00A0)+|(\\s|\\u00A0)+$)",
		flags: "g",
		replace: ""
	};
	return this.regExpFormatter.format(vValue, mRegExpAttributes);
};

/**
 * @private
 */
TrimFormatter.prototype.toString = function() {
	return "br.formatting.TrimFormatter";
};

module.exports = TrimFormatter;
