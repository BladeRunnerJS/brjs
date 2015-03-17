/**
 * @module br/formatting/LowerCaseFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');

/**
 * @class
 * @alias module:br/formatting/LowerCaseFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Converts a string to lower case.
 *
 * <p><code>LowerCaseFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluate to "hello, world!":</p>
 *
 * <pre>br.formatting.LowerCaseFormatter.format("Hello, World!", {})</pre>
 */
function LowerCaseFormatter() {}

topiarist.implement(LowerCaseFormatter, Formatter);

/**
 * Converts a string to lower case.
 *
 * @param {Variant} vValue  the string.
 * @param {Map} mAttributes  (unused)
 * @return  the string, converted to lower case.
 * @type  String
 */
LowerCaseFormatter.prototype.format = function(vValue, mAttributes) {
	return typeof(vValue) == "string" ? vValue.toLowerCase() : vValue
};

/**
 * @private
 */
LowerCaseFormatter.prototype.toString = function() {
	return "br.formatting.LowerCaseFormatter";
};

module.exports = LowerCaseFormatter;
