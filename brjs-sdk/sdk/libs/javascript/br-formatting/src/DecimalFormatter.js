/**
 * @module br/formatting/DecimalFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var NumberUtil = require('br/util/Number');

/**
 * @class
 * @alias module:br/formatting/DecimalFormatter
 * @implements module:br/formatting/Formatter
 * 
 * @classdesc
 * Formats the value to the specified number of decimal places.
 * <p/>
 * <code>DecimalFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "3.142":
 * 
 * If you are using this formatter in conjunction with the ThousandsFormatter in a localised application, please
 * ensure this DecimalFormatter is applied before the ThousandsFomatter, otherwise localised
 * decimal point characters will stop the DecimalFormatter from recognising the number.  
 * <p/>
 * <code>br.formatting.DecimalFormatter.format(3.14159, {dp:3})</code>
 */
function DecimalFormatter() {}

topiarist.implement(DecimalFormatter, Formatter);

/**
 * Formats the value to the specified number of decimal places.
 *
<p>
 * Attribute Options:
 * </p>
 * <p>
 * <table>
 * <tr>
 * <th>Option</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>dp</td><td>  the number of decimal places to apply.</td>
 * </tr>
 * </table>
 *
 * @param {Variant} vValue  the number (String or Number type).
 * @param {Map} mAttributes  the map of attributes.
 * @return  the number, formatted to the specified precision.
 * @type  String
 */
DecimalFormatter.prototype.format = function(vValue, mAttributes) {
	return NumberUtil.isNumber(vValue) ? NumberUtil.toFixed(vValue, mAttributes["dp"]) : vValue;
};

/**
 * @private
 */
DecimalFormatter.prototype.toString = function() {
	return "br.formatting.DecimalFormatter";
};

module.exports = DecimalFormatter;
