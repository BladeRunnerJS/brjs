/**
 * @class
 * 
 * Formats the value to the specified rounding.
 * <p/>
 * <code>RoundingFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "3.142":
 * <p/>
 * <code>br.presenter.formatter.RoundingFormatter.format(3.14159, {dp:3})</code>
 * 
 * @implements br.presenter.formatter.Formatter
 * @singleton
 */
br.presenter.formatter.RoundingFormatter = function() {
};

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
br.presenter.formatter.RoundingFormatter.prototype.format = function(vValue, mAttributes) {
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
br.presenter.formatter.RoundingFormatter.prototype.toString = function() {
	return "br.presenter.formatter.RoundingFormatter";
};

br.Core.implement(br.presenter.formatter.RoundingFormatter, br.presenter.formatter.Formatter);