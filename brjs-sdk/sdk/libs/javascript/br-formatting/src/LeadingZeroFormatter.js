/**
 * @module br/formatting/LeadingZeroFormatter
 */

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
br.formatting.LeadingZeroFormatter = function() {
};

br.Core.implement(br.formatting.LeadingZeroFormatter, br.formatting.Formatter);

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
br.formatting.LeadingZeroFormatter.prototype.format = function(vValue, mAttributes){
	return br.util.Number.pad(vValue, mAttributes["length"]);
}

/**
 * @private
 */
br.formatting.LeadingZeroFormatter.prototype.toString = function() {
	return "br.formatting.LeadingZeroFormatter";
};
