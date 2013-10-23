/**
 * @class
 * 
 * Converts the number to a percentage.
 * <p/>
 * <code>PercentFormatter<code/> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "61.8%":
 * <p/>
 * <code>br.presenter.formatter.PercentFormatter.format(0.618, {dp:1})</code>
 * 
 * @implements br.presenter.formatter.Formatter
 * @singleton
 */
br.presenter.formatter.PercentFormatter = function() {
	this.roundingFormatter = new br.presenter.formatter.RoundingFormatter();
};

br.implement(br.presenter.formatter.PercentFormatter, br.presenter.formatter.Formatter);

/**
 * Converts a decimal number to a percentage.
 * 
 * @param {Variant} vValue  the decimal number (String or Number type).
 * @param {Map} mAttributes  a list of attributes, as specified in {@link br.presenter.formatter.RoundingFormatter}.
 * @return  the number specified as a percentage.
 * @type  String
 */
br.presenter.formatter.PercentFormatter.prototype.format = function(vValue, mAttributes) {
	if (br.util.Number.isNumber(vValue)) {
		vValue = this.roundingFormatter.format(vValue * 100, mAttributes) + "%";
	}
	return vValue;		
};

/**
 * @private
 */
br.presenter.formatter.PercentFormatter.prototype.toString = function() {
	return "br.presenter.formatter.PercentFormatter";
};
