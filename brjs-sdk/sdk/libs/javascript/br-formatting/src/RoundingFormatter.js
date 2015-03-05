/**
 * @module br/formatting/RoundingFormatter
 */

/**
 * @class
 * @alias module:br/formatting/RoundingFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Formats the value to the specified rounding.
 *
 * <p><code>RoundingFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "3.142":</p>
 *
 * <pre>br.formatting.RoundingFormatter.format(3.14159, {dp:3})</pre>
 */
br.formatting.RoundingFormatter = function() {
};

br.Core.implement(br.formatting.RoundingFormatter, br.formatting.Formatter);

/**
 * Formats the number to the specified precision.
 *
 * <p>
 * Attribute Options:
 * </p>
 * <p>
 * <table>
 * <tr>
 * <th>Option</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>sf</td><td>  the number of significant figures to apply (optional)</td></tr>
 * <td>dp</td><td>  the number of decimal places to apply (optional)</td></tr>
 * <td>rounding</td><td>  the number of decimal places to be rounded, omitting any trailing zeros (optional)</td></tr>
 * </table>
 *
 * @param {Variant} vValue  the number.
 * @param {Map} mAttributes  the map of attributes.
 * @return  the number, formatted to the specified precision.
 * @type  String
 */
br.formatting.RoundingFormatter.prototype.format = function(vValue, mAttributes) {
	if (br.util.Number.isNumber(vValue)) {
		vValue = br.util.Number.toPrecision(vValue, mAttributes["sf"]);
		vValue = br.util.Number.toFixed(vValue, mAttributes["dp"]);
		vValue = br.util.Number.toRounded(vValue, mAttributes["rounding"]);
	}
	return vValue;
};

/**
 * @private
 */
br.formatting.RoundingFormatter.prototype.toString = function() {
	return "br.formatting.RoundingFormatter";
};
