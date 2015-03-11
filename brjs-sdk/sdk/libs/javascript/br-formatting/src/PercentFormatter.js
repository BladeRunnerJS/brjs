/**
 * @module br/formatting/PercentFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var NumberUtil = require('br/util/Number');
var RoundingFormatter = require('br/formatting/RoundingFormatter');

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
function PercentFormatter() {
	this.roundingFormatter = new RoundingFormatter();
}

topiarist.implement(PercentFormatter, Formatter);

/**
 * Converts a decimal number to a percentage.
 *
 * @param {Variant} vValue  the decimal number (String or Number type).
 * @param {Map} mAttributes  a list of attributes, as specified in {@link module:br/formatting/RoundingFormatter}.
 * @return  the number specified as a percentage.
 * @type  String
 */
PercentFormatter.prototype.format = function(vValue, mAttributes) {
	if (NumberUtil.isNumber(vValue)) {
		vValue = this.roundingFormatter.format(vValue * 100, mAttributes) + "%";
	}
	return vValue;
};

/**
 * @private
 */
PercentFormatter.prototype.toString = function() {
	return "br.formatting.PercentFormatter";
};

module.exports = PercentFormatter;
