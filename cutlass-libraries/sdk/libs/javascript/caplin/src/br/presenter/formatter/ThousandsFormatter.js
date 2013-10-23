/**
 * @fileoverview
 * 
 * Adds a separator character for each 'thousand' position in a 1234567.890 eg 1000000 becomes 1,000,000
 * 
 * This formatter must be run after the DecimalFormatter, otherwise different locale formats for
 * decimal places may confuse the DecimalFormatter
 * <p/>
 * The following attributes are required:
 * <p/>
 * <ul>
 * <li><code>separator</code> - the character to use as a thousands separator</li>
 * </ul>
 */

/**
 * ThousandsFormatter is a flyweight singleton, and therefore this constructor should never be invoked directly.
 * <p/>
 * Instead, it is instantiated by the RendererFactory, which reads RendererType specifications from XML and
 * instantiates the formatters by name.
 * 
 * @implements br.presenter.formatter.Formatter
 * @constructor
 */
br.presenter.formatter.ThousandsFormatter = function() {
	this.nullValueFormatter = new br.presenter.formatter.NullValueFormatter();
};

br.implement(br.presenter.formatter.ThousandsFormatter, br.presenter.formatter.Formatter);

/**
 * Adds a separator character for each 'thousand' position in a number. eg 1000000 becomes 1,000,000
 * 
 * @param {String} sValue  The field value
 * @param {Map} mAttributes  The renderer attributes
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
		var oTranslator = require("br/i18n").getTranslator();
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
