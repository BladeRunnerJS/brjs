/**
 * @module br/formatting/RoundingFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var NumberUtil = require('br/util/Number');

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
function RoundingFormatter() {}

topiarist.implement(RoundingFormatter, Formatter);

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
RoundingFormatter.prototype.format = function(vValue, mAttributes) {
	if (NumberUtil.isNumber(vValue)) {
		vValue = NumberUtil.toPrecision(vValue, mAttributes["sf"]);
		vValue = NumberUtil.toFixed(vValue, mAttributes["dp"]);
		vValue = NumberUtil.toRounded(vValue, mAttributes["rounding"]);
	}
	return vValue;
};

/**
 * @private
 */
RoundingFormatter.prototype.toString = function() {
	return "br.formatting.RoundingFormatter";
};

module.exports = RoundingFormatter;
