/**
 * @module br/parsing/LocalisedDateParser
 */

var topiarist = require('topiarist');
var Parser = require('br/parsing/Parser');
var moment = require('momentjs');

/**
 * @class
 * @alias module:br/parsing/LocalisedDateParser
 * @implements module:br/parsing/Parser
 * 
 * @classdesc
 * Matches a date string and converts it to a specified output format.
 * 
 * <p><code>LocalisedDateParser</code> is typically used with Presenter, but can be invoked programmatically.
 * It can make use of {@link http://momentjs.com/docs/#localized-formats|Moment.js localized formats} for input and output.</p>
 * 
 * <code>new LocalisedDateParser().parse('09/08/2000', {inputFormats: ['MM/DD/YYYY'], outputFormat: 'LL'})</code>
 *
 * <p>It uses {@link http://momentjs.com/docs/#/i18n/|Moment.js locale configuration} to parse localized dates.</p>
 * 
 * See {@link module:br/formatting/LocalisedDateFormatter} for the complementary formatter.
 */
function LocalisedDateParser() {}
topiarist.implement(LocalisedDateParser, Parser);

/**
 * Matches a date string and converts it to a specified output format.
 *
 * @param {string} date The date to parse
 * @param {object} attributes Map of configuration options
 * @param {string[]} attributes.inputFormats The possible input formats, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} attributes.outputFormat The output format, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} [attributes.locale] Locale override for the output
 * @param {boolean} [attributes.endOfUnit=false] If true, parse ambiguous dates to the end of the month or year
 * @returns {string} The date, expressed in the output format
 */
LocalisedDateParser.prototype.parse = function(date, attributes) {
	var parsedDate;
	var parseSuccessful = attributes.inputFormats.some(function(format) {
		parsedDate = parse(date, format, attributes);
		return parsedDate.isValid();
	});

	if (parseSuccessful) {
		if (typeof attributes.locale !== 'undefined') {
			parsedDate.lang(attributes.locale);
		}
		return parsedDate.format(attributes.outputFormat);
	}
};

/**
 */
LocalisedDateParser.prototype.isSingleUseParser = function() {
  return false;
};

/**
 * @private
 * @param {string} date The date to parse
 * @param {string} format The input format
 * @param {object} attributes Map of attributes
 * @returns {object} A Moment.js object
 */
function parse(date, format, attributes) {
	var parsedDate = moment(date, format);
	var lowerCaseFormat = format.toLowerCase();

	if (attributes.endOfUnit === true && lowerCaseFormat.indexOf('d') === -1) {
		parsedDate.endOf(lowerCaseFormat.indexOf('m') === -1 ? 'year' : 'month');
	}

	return parsedDate;
}

module.exports = LocalisedDateParser;
