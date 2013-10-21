/**
 * @class
 * 
 * Converts a string to lower case.
 * <p/>
 * <code>LowerCaseFormatter</code> is typically used in the XML Renderer Framework, but can be invoked programmatically
 * as in the following example which evaluate to "hello, world!"
 * <p/>
 * <code>br.presenter.formatter.LowerCaseFormatter.format("Hello, World!", {})</code>
 * 
 * @implements br.presenter.formatter.Formatter
 * @singleton
 */
br.presenter.formatter.LowerCaseFormatter = function() {
};

br.implement(br.presenter.formatter.LowerCaseFormatter, br.presenter.formatter.Formatter);

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
