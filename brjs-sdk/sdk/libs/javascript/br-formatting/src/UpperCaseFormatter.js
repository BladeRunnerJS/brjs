/**
 * @module br/formatting/UpperCaseFormatter
 */

/**
 * @class
 * @alias module:br/formatting/UpperCaseFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Converts a string to upper case.
 *
 * <p><code>UpperCaseFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluate to "hello, world!":</p>
 *
 * <pre>br.formatting.UpperCaseFormatter.format("Hello, World!", {})</pre>
 */
br.formatting.UpperCaseFormatter = function() {
};

br.Core.implement(br.formatting.UpperCaseFormatter, br.formatting.Formatter);

/**
 * Converts a string to lower case.
 *
 * @param {Variant} vValue  the string.
 * @param {Map} mAttributes  (unused)
 * @return  the string, converted to upper case.
 * @type  String
 */
br.formatting.UpperCaseFormatter.prototype.format = function(vValue, mAttributes) {
	return typeof(vValue) == "string" ? vValue.toUpperCase() : vValue
};

/**
 * @private
 */
br.formatting.UpperCaseFormatter.prototype.toString = function() {
	return "br.formatting.UpperCaseFormatter";
};
