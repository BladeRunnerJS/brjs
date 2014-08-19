/**
 * @module br/presenter/formatter/LowerCaseFormatter
 */

/**
 * @class
 * @alias module:br/presenter/formatter/LowerCaseFormatter
 * @implements module:br/presenter/formatter/Formatter
 * @singleton
 * 
 * @description
 * Converts a string to lower case.
 * 
 * <p><code>LowerCaseFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluate to "hello, world!":</p>
 * 
 * <pre>br.presenter.formatter.LowerCaseFormatter.format("Hello, World!", {})</pre>
 */
br.presenter.formatter.LowerCaseFormatter = function() {
};

br.Core.implement(br.presenter.formatter.LowerCaseFormatter, br.presenter.formatter.Formatter);

/**
 * Converts a string to lower case.
 * 
 * @param {Variant} vValue  the string.
 * @param {Map} mAttributes  (unused)
 * @return  the string, converted to lower case.
 * @type  String
 */
br.presenter.formatter.LowerCaseFormatter.prototype.format = function(vValue, mAttributes) {
	return typeof(vValue) == "string" ? vValue.toLowerCase() : vValue
};

/**
 * @private
 */
br.presenter.formatter.LowerCaseFormatter.prototype.toString = function() {
	return "br.presenter.formatter.LowerCaseFormatter";
};
