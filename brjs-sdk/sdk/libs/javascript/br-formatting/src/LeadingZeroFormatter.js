/**
 * @module br/formatting/LeadingZeroFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var NumberUtil = require('br/util/Number');

/**
 * @class
 * @alias module:br/formatting/LeadingZeroFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Pads the integer part of a number with as many leading zeros needed to reach the specified length.
 * <p/>
 * <code>LeadingZeroFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "0089":
 * <p/>
 * <code>br.formatting.LeadingZeroFormatter.format(89, {length:4})</code>
 */
function LeadingZeroFormatter() {}

topiarist.implement(LeadingZeroFormatter, Formatter);

/**
 * Pads the integer part of a number with as many leading zeros needed to reach the specified size.
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
 * <td>size</td><td> the minimum size of the padded number (defaults to zero)</td></tr>
 * </table>
 *
 * @param {Variant} vValue  the number (String or Number type).
 * @param {Map} mAttributes  the map of attributes.
 * @return  the number, padded to the required length with leading zeros.
 * @type  String
 */
LeadingZeroFormatter.prototype.format = function(vValue, mAttributes){
	return NumberUtil.pad(vValue, mAttributes.length);
};

/**
 * @private
 */
LeadingZeroFormatter.prototype.toString = function() {
	return "br.formatting.LeadingZeroFormatter";
};

module.exports = LeadingZeroFormatter;
