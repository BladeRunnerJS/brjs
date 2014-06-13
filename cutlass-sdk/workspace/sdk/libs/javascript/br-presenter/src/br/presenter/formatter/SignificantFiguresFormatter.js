/**
 * @class
 * 
 * Formats a number to the specified number of significant figures.
 * <p/>
 * <code>SignificantFiguresFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "3.142":
 * <p/>
 * <code>br.presenter.formatter.SignificantFiguresFormatter.format(3.14159, {sf:4})</code>
 * 
 * @implements br.presenter.formatter.Formatter
 * @singleton
 */
br.presenter.formatter.SignificantFiguresFormatter = function() {
};

br.Core.implement(br.presenter.formatter.SignificantFiguresFormatter, br.presenter.formatter.Formatter);

/**
 * Formats a number to the specified number of significant figures.
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
 * <td>sf</td><td>  the number of significant figures to apply (does nothing if omitted).</td></tr>
 * </table>
 *
 * @param {Variant} vValue  the number (String or Number type).
 * @param {Map} mAttributes  the map of attributes.
 * @return  the number, formatted to the specified precision.
 * @type  String
 */
br.presenter.formatter.SignificantFiguresFormatter.prototype.format = function(vValue, mAttributes) {
	return br.util.Number.isNumber(vValue) ? String(br.util.Number.toPrecision(vValue, mAttributes["sf"])) : vValue;
};

/**
 * @private
 */
br.presenter.formatter.SignificantFiguresFormatter.prototype.toString = function() {
	return "br.presenter.formatter.SignificantFiguresFormatter";
};
