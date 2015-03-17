/**
 * @module br/parsing/LocalisedDateParser
 */

var topiarist = require('topiarist');
var Parser = require('br/parsing/Parser');
var LocalisedDateParsingUtil = require('br/parsing/LocalisedDateParsingUtil');

/**
 * @class
 * @alias module:br/parsing/LocalisedDateParser
 * @implements module:br/parsing/Parser
 * 
 * @classdesc
 * Matches a date string and converts it to a specified output format. This supersedes {@link module:br/parsing/DateParser},
 * which although it does provide localisation, is not completely reliable.
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
function LocalisedDateParser() {
	this.localisedDateParsingUtil = new LocalisedDateParsingUtil();
}
topiarist.implement(LocalisedDateParser, Parser);

/**
 * Matches a date string and converts it to a specified output format.
 *
 * @param {string} date The date to parse
 * @param {object} attributes Map of configuration options
 * @param {string[]} attributes.inputFormats The possible input formats, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} attributes.outputFormat The output format, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} [attributes.inputLocale] Locale override for the input
 * @param {string} [attributes.outputLocale] Locale override for the output
 * @param {boolean} [attributes.endOfUnit=false] If true, parse ambiguous dates to the end of the month or year
 * @returns {string} The date, expressed in the output format
 */
LocalisedDateParser.prototype.parse = function(date, attributes) {
	return this.localisedDateParsingUtil.parse(date, attributes);
};

/**
 */
LocalisedDateParser.prototype.isSingleUseParser = function() {
  return false;
};

module.exports = LocalisedDateParser;
