/**
 * @module br/formatting/LocalisedDateFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var LocalisedDateParsingUtil = require('br/parsing/LocalisedDateParsingUtil');

/**
 * @class
 * @alias module:br/formatting/LocalisedDateFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Formats a date value by converting it from a specified input format to a new output format. This supersedes
 * {@link br/formatting/DateFormatter}, which although it does provide localisation, is not completely reliable.
 *
 * <p><code>LocalisedDateFormatter</code> is typically used with Presenter, but can be invoked programmatically.
 * It can make use of {@link http://momentjs.com/docs/#localized-formats|Moment.js localized formats} for input and output.</p>
 *
 * <code>new LocalisedDateFormatter().format('20150525', {inputFormat:'YYYYMMDD' outputFormat: 'LL'})</code>
 *
 * <p>It uses {@link http://momentjs.com/docs/#/i18n/|Moment.js locale configuration} to format localized dates.</p>
 *
 * See {@link module:br/parsing/LocalisedDateParser} for the complementary parser.
 */
function LocalisedDateFormatter() {
	this.localisedDateParsingUtil = new LocalisedDateParsingUtil();
}
topiarist.implement(LocalisedDateFormatter, Formatter);

/**
 * Formats a date by converting it from a specified input format to a new output format.
 *
 * @param {string} date The input date
 * @param {object} attributes Map of configuration options
 * @param {string} attributes.inputFormat Format of the input date, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} [attributes.outputFormat='L'] Format of the output date, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} [attributes.inputLocale] Locale override for the input
 * @param {string} [attributes.outputLocale] Locale override for the output
 * @returns {string} The date expressed in the output format, or the input value if it could not be parsed in the input format
 */
LocalisedDateFormatter.prototype.format = function(date, attributes) {
	attributes.inputFormats = [attributes.inputFormat];
	attributes.outputFormat = attributes.outputFormat || 'L';

	var result = this.localisedDateParsingUtil.parse(date, attributes);

	return typeof result === 'undefined' ? date : result;
};

module.exports = LocalisedDateFormatter;
