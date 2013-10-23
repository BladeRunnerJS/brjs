/**
 * @class
 * 
 * Converts a string to upper case.
 * <p/>
 * <code>UpperCaseFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluate to "hello, world!":
 * <p/>
 * <code>br.presenter.formatter.UpperCaseFormatter.format("Hello, World!", {})</code>
 * 
 * @implements br.presenter.formatter.Formatter
 * @singleton
 */
br.presenter.formatter.UpperCaseFormatter = function() {
};

br.implement(br.presenter.formatter.UpperCaseFormatter, br.presenter.formatter.Formatter);

/**
 * Converts a string to lower case.
 * 
 * @param {Variant} vValue  the string.
 * @param {Map} mAttributes  (unused)
 * @return  the string, converted to upper case.
 * @type  String
 */
br.presenter.formatter.UpperCaseFormatter.prototype.format = function(vValue, mAttributes) {
	return typeof(vValue) == "string" ? vValue.toUpperCase() : vValue
};

/**
 * @private
 */
br.presenter.formatter.UpperCaseFormatter.prototype.toString = function() {
	return "br.presenter.formatter.UpperCaseFormatter";
};
