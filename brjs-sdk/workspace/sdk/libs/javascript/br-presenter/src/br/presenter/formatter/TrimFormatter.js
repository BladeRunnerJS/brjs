/**
 * @module br/presenter/formatter/TrimFormatter
 */

/**
 * @singleton
 * @class
 * @alias module:br/presenter/formatter/TrimFormatter
 * @implements module:br/presenter/formatter/Formatter
 * 
 * @classdesc
 * Trims whitespace from boths ends of the string.
 */
br.presenter.formatter.TrimFormatter = function() {
	this.regExpFormatter = new br.presenter.formatter.RegExpFormatter();
};

br.Core.implement(br.presenter.formatter.TrimFormatter, br.presenter.formatter.Formatter);

/**
 * Trims whitespace from boths ends of the string.
 * <p/>
 * <code>TrimFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "Buy USD":
 * <p/>
 * <code>br.presenter.formatter.TrimFormatter.format(" Buy USD ", {})</code>
 *
 * @param {Variant} vValue  the string to trim.
 * @param {Map} mAttributes  the map of attributes.
 * @return  the trimmed string.
 * @type  String
 */
br.presenter.formatter.TrimFormatter.prototype.format = function(vValue, mAttributes) {
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
br.presenter.formatter.TrimFormatter.prototype.toString = function() {
	return "br.presenter.formatter.TrimFormatter";
};
