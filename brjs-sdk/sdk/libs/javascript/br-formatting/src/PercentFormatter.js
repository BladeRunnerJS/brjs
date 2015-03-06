/**
 * @module br/formatting/PercentFormatter
 */

/**
 * @class
 * @alias module:br/formatting/PercentFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Converts the number to a percentage.
 *
 * <p><code>PercentFormatter<code/> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "61.8%":</p>
 *
 * <pre>br.formatting.PercentFormatter.format(0.618, {dp:1})</pre>
 */
br.formatting.PercentFormatter = function() {
	this.roundingFormatter = new br.formatting.RoundingFormatter();
};

br.Core.implement(br.formatting.PercentFormatter, br.formatting.Formatter);

/**
 * Converts a decimal number to a percentage.
 *
 * @param {Variant} vValue  the decimal number (String or Number type).
 * @param {Map} mAttributes  a list of attributes, as specified in {@link module:br/formatting/RoundingFormatter}.
 * @return  the number specified as a percentage.
 * @type  String
 */
br.formatting.PercentFormatter.prototype.format = function(vValue, mAttributes) {
	if (br.util.Number.isNumber(vValue)) {
		vValue = this.roundingFormatter.format(vValue * 100, mAttributes) + "%";
	}
	return vValue;
};

/**
 * @private
 */
br.formatting.PercentFormatter.prototype.toString = function() {
	return "br.formatting.PercentFormatter";
};
