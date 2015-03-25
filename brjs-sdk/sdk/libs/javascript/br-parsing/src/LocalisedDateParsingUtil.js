/**
 * @module br/parsing/LocalisedDateParsingUtil
 */

var moment = require('momentjs');
var LocaleService = require('service!br.locale-service');

/**
 * @class
 * @alias module:br/parsing/LocalisedDateParsingUtil
 * 
 * @classdesc Utility class for parsing dates
 */
function LocalisedDateParsingUtil() {
	this.locale = LocaleService.getLocale().replace('_', '-');
}

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
LocalisedDateParsingUtil.prototype.parse = function(date, attributes) {
	var inputLocale = attributes.inputLocale || this.locale;
	var outputLocale = attributes.outputLocale || this.locale;
	var parsedDate;

	var parseSuccessful = attributes.inputFormats.some(function(format) {
		var lowerCaseFormat = format.toLowerCase();
		parsedDate = moment(date, format, inputLocale, true);

		if (attributes.endOfUnit === true && lowerCaseFormat.indexOf('d') === -1 && lowerCaseFormat.indexOf('l') === -1) {
			parsedDate.endOf(lowerCaseFormat.indexOf('m') === -1 ? 'year' : 'month');
		}

		return parsedDate.isValid();
	});

	if (parseSuccessful) {
		parsedDate.lang(outputLocale);
		return parsedDate.format(attributes.outputFormat);
	}
};

module.exports = LocalisedDateParsingUtil;
