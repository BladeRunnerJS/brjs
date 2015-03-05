/**
 * @module br/parsing/DateParser
 */

var topiarist = require('topiarist');
var Parser = require('br/parsing/Parser');
var DateFormatter = require('br/formatting/DateFormatter');
var RegExpUtil = require('br/util/RegExp');

/**
 * @class
 * @alias module:br/parsing/DateParser
 * @implements module:br/parsing/Parser
 * 
 * @classdesc
 * Matches a date string and converts it to a specified output format.
 * 
 * <p><code>DateParser</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "08-Sep-2000":</p>
 * 
 * <pre>new DateParser().parse("09/08/2000", {american:"true", outputFormat:"d-M-Y"})</pre>
 * 
 * See {@link module:br/formatting/DateFormatter} for the complementary formatter.
 */
function DateParser() {
	this.m_pAmericanFormats = ["MM-DD-YYYY", "MM-DD-YY", "MMM-DD-YYYY", "MMM-DD-YY", "MM-DD"];
	this.m_pEuropeanFormats = ["DD-MM-YYYY", "DD-MMM-YYYY", "DD-MM", "DD-MM-YY", "DDMMYY", "DDMMYYYY"];
	this.m_pCommonFormats = [
		"DD-MMM-YYYY HH:mm:ss", "DD-MMM-YY HH:mm:ss", "DD-MMM-YYYY HH:mm", "YYYY-MM-DD", "YYYYMMDDHHmmss", 
		"YYYYMMDDHHmm", "YYYYMMDD"
	];
	this.m_sSeparatorsDefault = "\\/.-";
	this.m_oDateFormatter = new DateFormatter();
}
topiarist.implement(DateParser, Parser);

/**
 * Matches a date string and converts it to a specified output format.
 *
 * In order to match the date, either a list of explicit <code>inputFormats</code> can be supplied, or
 * an <code>american</code> indicator can be used against a set of standard formats, which are as follows:
 * <p/>
 * American: m-d-Y, m-d-y, M-d-Y, M-d-y, m-d<br/>
 * European: d-m-Y, d-m-Y, d-M-Y, d-M-Y, d-m<br/>
 * <p/>
 *
 * @param {string|Date} vValue the date to parse.
 * @param {object} mAttributes the map of attributes.
 * @param {boolean} [mAttributes.american=false] if true, dates are assumed to be in American format, i.e. month before date
 * @param {string} [mAttributes.separators='/.-'] a set of admissible separator characters
 * @param {string} mAttributes.inputFormats a comma separated list of admissible input formats
 * @param {string} mAttributes.outputFormat the output date format
 * @param {boolean} [mAttributes.endOfUnit=false] if true, parse ambiguous dates to the end of the month or year
 * @return {string} the date, expressed in the output format
 * @type String
 */
DateParser.prototype.parse = function(vValue, mAttributes) {
	if (vValue) {
		var vDate = this._standardizeDateSeparators(vValue, mAttributes);
		var pInputFormats = this._getAdmissibleInputFormats(mAttributes);
		vValue = this._matchDate(vDate, pInputFormats, mAttributes.outputFormat, mAttributes);
	}
	return vValue;
};

DateParser.prototype.isSingleUseParser = function() {
  return false;
};

/**
 * @static
 * @param {string|Date} vDate The date to parse
 * @param {string} sDateFormat The input format
 * @param {object} [mAttributes] A map of options
 * @param {boolean} [mAttributes.endOfUnit=false] Whether to parse ambiguous dates to the end of a month or year
 * @returns {Date}
 */
DateParser.parseDate = function(vDate, sDateFormat, mAttributes) {
	if (!vDate)
	{
		return null;
	}
	if (vDate instanceof Date)
	{
		sDateFormat = "javascript";
	}
	else if (!sDateFormat)
	{
		sDateFormat = "DD-MM-YYYY HH:mm:ss";
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
			var oMoment = moment(String(vDate), sDateFormat);
			if (mAttributes && mAttributes.endOfUnit === true && sDateFormat.toLowerCase().indexOf('d') === -1) {
				oMoment.endOf(sDateFormat === 'YYYY' ? 'year' : 'month');
			}
			var sValidationString = oMoment.format(sDateFormat);
			return (sValidationString.toLowerCase() == String(vDate).toLowerCase()) ? oMoment.toDate() : null;
	}
};

/**
 * @private
 */
DateParser.prototype._matchDate = function(vDate, pInputFormats, sOutputFormat, mAttributes) {
	for (var i = 0, n = pInputFormats.length; i < n; ++i) {
		var oDate = br.parsing.DateParser.parseDate(vDate, pInputFormats[i], mAttributes);
		if (oDate) {
			return this.m_oDateFormatter.formatDate(oDate, sOutputFormat);
		}
	}
};

/**
 * @private
 */
DateParser.prototype._standardizeDateSeparators = function(vDate, mAttributes) {
	if (vDate.constructor === String) {
		var sRegExp = "[" + RegExpUtil.escape(mAttributes.separators || this.m_sSeparatorsDefault) + "]";
		var oNonStandardSeparatorRegExp = new RegExp(sRegExp, "g");
		vDate = vDate.replace(oNonStandardSeparatorRegExp, "-");
	}
	return vDate;
};

/**
 * @private
 */
DateParser.prototype._getAdmissibleInputFormats = function(mAttributes) {
	return mAttributes.inputFormats ? mAttributes.inputFormats.split(",") : this._getDefaultInputFormats(mAttributes);
};

/**
 * @private
 */
DateParser.prototype._getDefaultInputFormats = function(mAttributes) {
	return this.m_pCommonFormats.concat((mAttributes.american == "true") ? this.m_pAmericanFormats : this.m_pEuropeanFormats);
};

/**
 * @private
 */
DateParser.prototype.toString = function() {
	return "DateParser";
};

module.exports = DateParser;
