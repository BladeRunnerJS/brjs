/**
 * @fileoverview
 * 
 * Adds a separator character for each 'thousand' position in a 1234567.890 eg 1000000 becomes 1,000,000
 * 
 * 
 * </ul>
 */

/**
 * Adds a separator character for each 'thousand' position (e.g. 1,000,000).
 * 
 * <p><code>ThousandsFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * too. This formatter must be run after the {@link br.presenter.formatter.DecimalFormatter}, otherwise different locale formats for
 * decimal places may confuse the <code>DecimalFormatter</code>.</p>
 * 
 * <p>The following attributes are required:<p/>
 * 
 * <ul>
 *   <li><code>separator</code> - the character to use as a thousands separator</li>
 * </ul>
 * 
 * @implements br.presenter.formatter.Formatter
 * @constructor
 */
br.presenter.formatter.ThousandsFormatter = function() {
	this.nullValueFormatter = new br.presenter.formatter.NullValueFormatter();
};

br.Core.implement(br.presenter.formatter.ThousandsFormatter, br.presenter.formatter.Formatter);

/**
 * Adds a separator character for each 'thousand' position in a number. eg 1000000 becomes 1,000,000
 * 
 * @param {String} sValue  The field value
 * @param {Map} mAttributes  The formatter attributes
 * @return The formatted value
 * @type String
 */
br.presenter.formatter.ThousandsFormatter.prototype.format = function(vValue, mAttributes) {
	vValue = vValue === 0 ? 0 : this.nullValueFormatter.format(vValue, mAttributes);
	try
	{
		var sNumber = String(vValue);
		var sNumberWithoutSuffix = this._stripSuffix(sNumber);
		var sNumberWithoutComma = this._stripComma(sNumberWithoutSuffix);
		var oTranslator = require("br/I18n").getTranslator();
		var sFormattedNumber = oTranslator.formatNumber(sNumberWithoutComma, mAttributes["separator"]);
		vValue = sNumber.replace(sNumberWithoutSuffix, sFormattedNumber);
	}
	catch (oException) {
	}
	return vValue;
};

/**
 * Extracts the number value from the input value. ie (1000MM) becomes 1000.
 * 
 * @param {String} vValue  The value to be formatted.
 * @return The number value or null if one cannot be found
 * @private
 */
br.presenter.formatter.ThousandsFormatter.prototype._stripSuffix = function(sValue) {
	var sMatch = sValue.match(/(\d+(,?\d)?)+(\.\d+)?/);
	return sMatch != null ? sMatch[0] : sValue;
};

br.presenter.formatter.ThousandsFormatter.prototype._stripComma = function(sValue) {
	return sValue.replace(/,/g, "");
};

/**
 * Returns a human-readable string representation of the object, which is useful for debugging.
 * 
 * @return  The string representation
 * @type String
 */
br.presenter.formatter.ThousandsFormatter.prototype.toString = function() {
	return "br.presenter.formatter.ThousandsFormatter";
};
