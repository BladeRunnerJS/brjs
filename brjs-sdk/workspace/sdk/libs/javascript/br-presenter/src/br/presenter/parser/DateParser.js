/**
 * @module br/presenter/parser/DateParser
 */

/**
 * @classdesc
 *
 * Matches a date string and converts it to a specified output format.
 * <p/>
 * <code>DateParser</code> is typically used with Presenter, but can be invoked programmatically
 * as in the following example which evaluates to "08-Sep-2000"
 * <p/>
 * <code>br.presenter.parser.DateParser.parse("09/08/2000", {american:"true", outputFormat:"d-M-Y"})</code>
 * <p/>
 * See {@link module:br/presenter/formatter/DateFormatter} for the complementary formatter.
 *
 * @singleton
 *
 * @implements module:br/presenter/parser/Parser
 */
br.presenter.parser.DateParser = function()
{
	this.m_pAmericanFormats = ["MM-DD-YYYY", "MM-DD-YY", "MMM-DD-YYYY", "MMM-DD-YY", "MM-DD"];
	this.m_pEuropeanFormats = ["DD-MM-YYYY", "DD-MMM-YYYY", "DD-MM", "DD-MM-YY", "DDMMYY", "DDMMYYYY"];
	this.m_pCommonFormats = [
		"DD-MMM-YYYY HH:mm:ss", "DD-MMM-YY HH:mm:ss", "DD-MMM-YYYY HH:mm", "YYYY-MM-DD", "YYYYMMDDHHmmss", 
		"YYYYMMDDHHmm", "YYYYMMDD"
	];
	this.m_sSeparatorsDefault = "\\/.-";
	this.m_oDateFormatter = new br.presenter.formatter.DateFormatter();
};
br.Core.implement(br.presenter.parser.DateParser, br.presenter.parser.Parser);

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
 *
 * <td>american</td><td>  if true, dates are assumed to be in American format, i.e. month before date (defaults to false)</td></tr>
 * <tr><td>separators</td><td>  a set of admissible separator characters (defaults to "/.-")</td></tr>
 * <tr><td>inputFormats</td><td>  a comma separated list of admissible input formats</td></tr>
 * <tr><td>outputFormat</td><td>  the output date format</td></tr>
 * </table>
 *
 * @param {Variant} vValue  the date to parse (String).
 * @param {Map} mAttributes  the map of attributes.
 * @return  the date, expressed in the output format
 * @type String
 */
br.presenter.parser.DateParser.prototype.parse = function(vValue, mAttributes) {
	if (vValue) {
		var vDate = this._standardizeDateSeparators(vValue, mAttributes);
		var pInputFormats = this._getAdmissibleInputFormats(mAttributes);
		var sOutputFormat = mAttributes.outputFormat;
		vValue = this._matchDate(vDate, pInputFormats, sOutputFormat);
	}
	return vValue;
};

/**
 * @private
 */
br.presenter.parser.DateParser.prototype._matchDate = function(vDate, pInputFormats, sOutputFormat) {
	for (var i = 0, n = pInputFormats.length; i < n; ++i) {
		var sInputFormat = pInputFormats[i];
		var oDate = this.m_oDateFormatter.parseDate(vDate, sInputFormat);
		if (oDate) {
			return this.m_oDateFormatter.formatDate(oDate, sOutputFormat);
		}
	}
};

/**
 * @private
 */
br.presenter.parser.DateParser.prototype._standardizeDateSeparators = function(vDate, mAttributes) {
	if (vDate.constructor === String) {
		var sRegExp = "[" + br.util.RegExp.escape(mAttributes["separators"] || this.m_sSeparatorsDefault) + "]";
		var oNonStandardSeparatorRegExp = new RegExp(sRegExp, "g");
		vDate = vDate.replace(oNonStandardSeparatorRegExp, "-");
	}
	return vDate;
};

/**
 * @private
 */
br.presenter.parser.DateParser.prototype._getAdmissibleInputFormats = function(mAttributes) {
	return mAttributes.inputFormats ? mAttributes.inputFormats.split(",") : this._getDefaultInputFormats(mAttributes);
};

/**
 * @private
 */
br.presenter.parser.DateParser.prototype._getDefaultInputFormats = function(mAttributes) {
	return this.m_pCommonFormats.concat((mAttributes["american"] == "true") ? this.m_pAmericanFormats : this.m_pEuropeanFormats);
};

/**
 * @private
 */
br.presenter.parser.DateParser.prototype.toString = function() {
	return "br.presenter.parser.DateParser";
};
