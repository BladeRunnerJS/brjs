/**
 * @module br/formatting/DateFormatter
 */

var moment = require('momentjs');
var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var DateParsingUtil = require('br/parsing/DateParsingUtil');

/**
 * @deprecated The functionality provided by this formatter can be achieved more reliably with {@link module:br/formatting/LocalisedDateFormatter}
 * @class
 * @alias module:br/formatting/DateFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Formats a date value by converting it from a specified input format to a new output format.
 * <p/>
 * <code>DateFormatter</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following examples which evaluate to "09-Sep-2001 01:46:40" and "2001Sep09" respectively:
 * <p/>
 * <code>new br.formatting.DateFormatter().format(1e12, {inputFormat:"U"})</code><br/>
 * <code>new br.formatting.DateFormatter().format(1e12, {inputFormat:"U", outputFormat:"YMd"})</code>
 * <p/>
 *
 * See {@link module:br/presenter/parser/DateParser} for the complementary parser.
 */
function DateFormatter() {}

topiarist.implement(DateFormatter, Formatter);

/**
 * Formats a date by converting it from a specified input format to a new output format.
 *
 * @param {string|Date} vValue the input date
 * @param {object} mAttributes the map of attributes.
 * @param {string} [mAttributes.inputFormat='DD-MM-YYYY HH:mm:ss'] format of the input date, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} [mAttributes.outputFormat='DD-MM-YYYY HH:mm:ss'] format of the output date, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {boolean} [mAttributes.adjustForTimezone=false] whether the formatter should adjust the date according to the client's timezone
 * @return  the output date.
 * @type String
 */
DateFormatter.prototype.format = function(vValue, mAttributes) {
	if (vValue) {
		var oDate = DateParsingUtil.parse(vValue, mAttributes.inputFormat);
		if (oDate) {
			vValue = this.formatDate(oDate, mAttributes.outputFormat, mAttributes);
		}
	}
	return vValue;
};

/**
 * @private
 * @deprecated
 */
DateFormatter.prototype.parseDate = function(vDate, sDateFormat) {
	return DateParsingUtil.parse(vDate, sDateFormat);
};

/**
 * @private
 */
DateFormatter.prototype.formatDate = function(oDate, sDateFormat, mAttributes) {
	var oTranslator = require("br/I18n").getTranslator();
	if(mAttributes && mAttributes.adjustForTimezone)
	{
		oDate = this._adjustDateForTimezone(oDate);
	}
	if (!sDateFormat)
	{
		sDateFormat = "DD-MM-YYYY HH:mm:ss";
	}
	switch (sDateFormat) {
		case "java":
			return String(oDate.getTime());
		case "javascript":
			return oDate;
		case "ISO":
			return oDate.toISOString();
		case "localize":
			return oTranslator.formatDate(oDate);
		default:
			return oTranslator.formatDate(oDate, sDateFormat);
	}
};

/**
 * @private
 */
DateFormatter.prototype._adjustDateForTimezone = function(oDate) {
	var oDateClone = new Date(oDate.getTime()),
		d = new Date(),
		timezoneOffsetInMinutes = -(d.getTimezoneOffset());

	oDateClone.setMinutes(oDate.getMinutes() + timezoneOffsetInMinutes);

	return oDateClone;
};

/**
 * @private
 */
DateFormatter.prototype.toString = function() {
	return "br.formatting.DateFormatter";
};

module.exports = DateFormatter;
