br.thirdparty("momentjs");

/**
 * @class
 * 
 * Formats a date value by converting it from a specified input format to a new output format.
 * <p/>
 * <code>DateFormatter</code> is typically used in the XML Renderer Framework, but can be invoked programmatically
 * as in the following examples which evaluate to "09-Sep-2001 01:46:40" and "2001Sep09" respectively:
 * <p/>
 * <code>br.presenter.formatter.DateFormatter.format(1e12, {inputFormat:"U"})</code><br/>
 * <code>br.presenter.formatter.DateFormatter.format(1e12, {inputFormat:"U", outputFormat:"YMd"})</code>
 * <p/>
 * See {@link br.presenter.parser.DateParser} for the complementary parser.
 *
 * @singleton
 *
 * @implements br.presenter.formatter.Formatter
 */
br.presenter.formatter.DateFormatter = function()
{
	this.m_sFormatDefault = "DD-MM-YYYY HH:mm:ss";
};

br.implement(br.presenter.formatter.DateFormatter, br.presenter.formatter.Formatter);

/**
 * Formats a date by converting it from a specified input format to a new output format.
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
 * <td>inputFormat</td><td>  format of the input date, expressed with <a href="http://momentjs.com/docs/#/parsing/string-format/">Moment.js format tokens</a> (defaults to DD-MM-YYYY HH:mm:ss).</td></tr>
 * <tr><td>outputFormat</td><td>format of the output date, expressed with <a href="http://momentjs.com/docs/#/displaying/format/">Moment.js format tokens</a> (defaults to DD-MM-YYYY HH:mm:ss).</td></tr>
 * <tr><td>adjustForTimezone</td><td> boolean value representing whether the formatter should adjust the date according to the client's timezone</td></tr>
 * </table>
 * 
 * @param {Variant} vValue  the input date (String or Date type).
 * @param {Map} mAttributes  the map of attributes.
 * @return  the output date.
 * @type String
 */
br.presenter.formatter.DateFormatter.prototype.format = function(vValue, mAttributes) {
	if (vValue) {
		var sInputFormat = mAttributes["inputFormat"];
		var bAdjustForTimezone = mAttributes["adjustForTimezone"] == "true" ? true : false;
		var oDate = this.parseDate(vValue, sInputFormat);
		if (oDate) {
			var sOutputFormat = mAttributes["outputFormat"];
			vValue = this.formatDate(oDate, sOutputFormat, bAdjustForTimezone);
		}
	}
	return vValue;
};

/**
 * @private
 * 
 * Convert from a specified date format to a JavaScript date object
 */
br.presenter.formatter.DateFormatter.prototype.parseDate = function(vDate, sDateFormat) 
{
	if (!vDate)
	{
		return null;
	}
	if (vDate.constructor == Date)
	{
		sDateFormat = "javascript";
	}
	else if (!sDateFormat)
	{
		sDateFormat = this.getDateFormat(sDateFormat);
	}
	
	switch (sDateFormat) {
		case "java":
			var oDate = new Date();
			oDate.setTime(Number(vDate));
			return oDate;
		case "javascript":
			return vDate;
		case "U":
			return moment(vDate*1000).toDate();
		default:
			oMoment = moment(String(vDate), sDateFormat);
			sValidationString = oMoment.format(sDateFormat);
			return (sValidationString == String(vDate)) ? oMoment.toDate() : null;
	}
};

/**
 * @private
 * 
 * Convert from a JavaScript date object to a specified format 
 */
br.presenter.formatter.DateFormatter.prototype.formatDate = function(oDate, sDateFormat, bAdjustForTimezone) {
	if(bAdjustForTimezone)
	{
		
		oDate = this._adjustDateForTimezone(oDate);
	}
	sDateFormat = this.getDateFormat(sDateFormat);
	switch (sDateFormat) {
		case "java":
			return String(oDate.getTime());
		case "javascript":
			return oDate;
		case "ISO":
			return oDate.toISOString();
		case "localize":
			var oTranslator = require("br/i18n").getTranslator();
			return oTranslator.formatDate(oDate);
		default:
			var oTranslator = require("br/i18n").getTranslator();
			return oTranslator.formatDate(oDate, sDateFormat);
	}
};

/**
 * @private
 */
br.presenter.formatter.DateFormatter.prototype._adjustDateForTimezone = function(oDate) {
	var oDateClone = new Date(oDate.getTime());
	var d = new Date();
	var timezoneOffsetInHours = -(d.getTimezoneOffset()/60);
	oDateClone.setHours(oDate.getHours() + timezoneOffsetInHours);
	return oDateClone;
};

/**
 * @private
 */
br.presenter.formatter.DateFormatter.prototype.getDateFormat = function(sDateFormat) {
	return sDateFormat || this.m_sFormatDefault;
};

/**
 * @private
 */
br.presenter.formatter.DateFormatter.prototype.toString = function() {
	return "br.presenter.formatter.DateFormatter";
};
