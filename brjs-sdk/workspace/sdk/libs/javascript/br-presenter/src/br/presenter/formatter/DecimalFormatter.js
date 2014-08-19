/**
 * @module br/presenter/formatter/DecimalFormatter
 */

/**
 * @singleton
 * @class
 * @alias module:br/presenter/formatter/DecimalFormatter
 * @implements module:br/presenter/formatter/Formatter
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
 * <code>br.presenter.formatter.DecimalFormatter.format(3.14159, {dp:3})</code>
 */
br.presenter.formatter.DecimalFormatter = function() {
};

br.Core.implement(br.presenter.formatter.DecimalFormatter, br.presenter.formatter.Formatter);

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
br.presenter.formatter.DecimalFormatter.prototype.format = function(vValue, mAttributes) {
	return br.util.Number.isNumber(vValue) ? br.util.Number.toFixed(vValue, mAttributes["dp"]) : vValue;
};

/**
 * @private
 */
br.presenter.formatter.DecimalFormatter.prototype.toString = function() {
	return "br.presenter.formatter.DecimalFormatter";
};
