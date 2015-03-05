/**
 * @module br/formatting/LowerCaseFormatter
 */

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
br.formatting.LowerCaseFormatter = function() {
};

br.Core.implement(br.formatting.LowerCaseFormatter, br.formatting.Formatter);

/**
 * Converts a string to lower case.
 *
 * @param {Variant} vValue  the string.
 * @param {Map} mAttributes  (unused)
 * @return  the string, converted to lower case.
 * @type  String
 */
br.formatting.LowerCaseFormatter.prototype.format = function(vValue, mAttributes) {
	return typeof(vValue) == "string" ? vValue.toLowerCase() : vValue
};

/**
 * @private
 */
br.formatting.LowerCaseFormatter.prototype.toString = function() {
	return "br.formatting.LowerCaseFormatter";
};
