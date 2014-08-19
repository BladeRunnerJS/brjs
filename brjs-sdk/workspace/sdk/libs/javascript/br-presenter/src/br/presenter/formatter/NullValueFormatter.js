/**
 * @module br/presenter/formatter/NullValueFormatter
 */

/**
 * @singleton
 * @class
 * @alias module:br/presenter/formatter/NullValueFormatter
 * @implements module:br/presenter/formatter/Formatter
 * 
 * @description
 * Substitutes text when the value is <code>null</code>, <code>undefined</code>, or the empty string.
 * 
 * <p><code>NullValueFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "N/A":</p>
 * 
 * <pre>br.presenter.formatter.NullValueFormatter.format("", {nullValue:"N/A"})</pre>
 */
br.presenter.formatter.NullValueFormatter = function()
{
	this.m_sNullValueDefault = "\u00a0";
};

br.Core.implement(br.presenter.formatter.NullValueFormatter, br.presenter.formatter.Formatter);

/**
 * Substitutes replacement text when the string is void (null, undefined, or the empty string).
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
 * <td>nullValue</td><td>  the replacement text to substitute in case of a null string (defaults to an empty string)</td></tr>
 * </table>
 *
 * @param {Variant} vValue  the string.
 * @param {Map} mAttributes  the map of attributes.
 * @return  the replacement string in the case of a void, otherwise the unchanged string.
 * @type  String
 */
br.presenter.formatter.NullValueFormatter.prototype.format = function(vValue, mAttributes) {
	return (vValue == undefined || vValue == null ||  vValue == "") ? mAttributes["nullValue"] == null ? this.m_sNullValueDefault : mAttributes["nullValue"] : vValue;
};

/**
 * @private
 */
br.presenter.formatter.NullValueFormatter.prototype.toString = function() {
	return "br.presenter.formatter.NullValueFormatter";
};
