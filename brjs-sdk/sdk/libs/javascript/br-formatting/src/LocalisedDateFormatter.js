/**
 * @module br/formatting/LocalisedDateFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');
var LocalisedDateParser = require('br/parsing/LocalisedDateParser');

/**
 * @class
 * @alias module:br/formatting/LocalisedDateFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Formats a date value by converting it from a specified input format to a new output format.
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
function LocalisedDateFormatter() {}
topiarist.implement(LocalisedDateFormatter, Formatter);

/**
 * Formats a date by converting it from a specified input format to a new output format.
 *
 * @param {string} date The input date
 * @param {object} attributes Map of configuration options
 * @param {string} attributes.inputFormat Format of the input date, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} [attributes.outputFormat='L'] Format of the output date, expressed with {@link http://momentjs.com/docs/#/parsing/string-format/|Moment.js format tokens}
 * @param {string} [attributes.locale] Locale override for the output
 * @returns {string} The date, expressed in the output format
 */
LocalisedDateFormatter.prototype.format = function(date, attributes) {
	var parseAttributes = {
		inputFormats: [attributes.inputFormat],
		outputFormat: attributes.outputFormat || 'L',
		locale: attributes.locale
	};
	return LocalisedDateParser.prototype.parse(date, parseAttributes);
};

module.exports = LocalisedDateFormatter;
