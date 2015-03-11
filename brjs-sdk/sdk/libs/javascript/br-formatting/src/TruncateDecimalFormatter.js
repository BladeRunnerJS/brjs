/**
 * @module br/formatting/TruncateDecimalFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var NumberUtil = require('br/util/Number');

/**
 * @class
 * @alias module:br/formatting/TruncateDecimalFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Truncates the value to the specified number of decimal places.
 *
 * <p><code>TruncateDecimalFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "3.142":</p>
 *
 * <pre>br.formatting.TruncateDecimalFormatter.format(3.14159, {dp:3})</pre>
 */
function TruncateDecimalFormatter() {}

topiarist.implement(TruncateDecimalFormatter, Formatter);

/**
 * Truncates the value to the specified number of decimal places.  If the value has already fewer
 * decimal places than the supplied attribute, then the value is not changed.
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
 *<td>dp</td><td>  the number of decimal places at which to truncate.</td></tr>
 *</table>
 *
 * @param {Variant} vValue the number (String or Number type).
 * @param {Map} mAttributes the map of attributes.
 * @return  the number formatted to the specified precision.
 * @type  String
 */
TruncateDecimalFormatter.prototype.format = function(vValue, mAttributes) {
	if (NumberUtil.isNumber(vValue)) {
		var sValue = String(vValue);
		var sUnFormattedValue = this._removeExponent(sValue);
		var sFormattedValue = NumberUtil.toFixed(vValue, mAttributes["dp"]);
		vValue = this._getShorter(sFormattedValue, sUnFormattedValue);
	}
	return vValue;
};

/**
 * @private
 */
TruncateDecimalFormatter.prototype._removeExponent = function(s) {
	return s.indexOf("e") >= 0 ? String(Number(s)) : s;
};

/**
 * @private
 */
TruncateDecimalFormatter.prototype._getShorter = function(s1, s2) {
	return s1.length < s2.length ? s1 : s2;
};

/**
 * @private
 */
TruncateDecimalFormatter.prototype.toString = function() {
	return "br.formatting.TruncateDecimalFormatter";
};

module.exports = TruncateDecimalFormatter;
