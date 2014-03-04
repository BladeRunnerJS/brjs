"use strict";

var LocalisedNumber = require('./LocalisedNumber');
// LocalisedDate and LocalisedTime use br/i18n which depends on this class,
// so they have to be required where they are used or there would be a circular
// dependency.

var regExp = /\@\{(.*?)\}/m;
var TEST_DATE_FORMAT_SHORT = "d-m-Y";
var TEST_DATE_FORMAT_LONG = "D, d M, Y, h:i:s A";


/**
 * Do not instantiate this class directly. To access the localization token
 * translator use the global <code>br.i18n(token, args)</code> function which
 * maps to the <code>getMessage()</code> function.
 *
 * @see Translator#getMessage
 * @constructor
 *
 * @class
 * The class within the <code>caplin.i18n</code> package that is responsible
 * for translating localization tokens in the form of
 * <code>&#64;{key.name}</code> into translated text.
 */
function Translator(messages) {
	var unproccessedMessages = messages;

	/** @private */
	this.messages = {};

	for (var message in unproccessedMessages) {
		this.messages[message.toLowerCase()] = unproccessedMessages[message];
	}

	/** @private */
	this.localizationPrefs = {};
	/** @private */
	this.testMode = false;
};

/**
 * Translate is used to convert raw localization tokens in the form
 * <code>&#64;{key.name}</code> into translated text.
 *
 * <p>By default this method also converts reserved XML characters (<,>,",',&)
 * into XML entity references (> into &gt; etc). If you require raw text
 * translation without the XML entity reference conversion, pass a type of
 * "text" as an argument to this method.</p>
 *
 * @param {String} sText The string within which to replace localization tokens.
 * @param {String} sType The type of text to translate (defaults to "xml", pass
 *      "text" for translation without XML entity reference conversion).
 * @type String
 * @returns A string with localization tokens replaced with the current locale's
 *         messages.
 */
Translator.prototype.translate = function(text, type) {
	var message;
	var match = regExp.exec(text);
	type = type || "xml";
	while (match) {
		message = this._getTranslationForKey(match[1]);
		if (type == "xml") {
			message = this.convertXMLEntityChars(message);
		}
		text = text.replace(match[0], message);
		match = regExp.exec(text);
	}
	return text;
};

/**
 * Returns whether the current locale contains a given localization token.
 *
 * <p>Usage: <code>Translator.getTranslator().tokenExists("br.core.field.start.date")</code></p>
 *
 * @param {String} sText The token name
 * @type boolean
 * @returns <code>true</code> if the localization token exists in the current locale's
 *         translation set, otherwise <code>false</code>.
 */
Translator.prototype.tokenExists = function(token) {
	return token.toLowerCase() in this.messages;
};

/**
 * @private
 *
 * <p>Change the translation list for testing i18n purposes</p>
 *
 * @param {Map} messages
 */
Translator.prototype._setMessages = function(messages) {
	this.messages = messages;
};

/**
 * Converts XML reserved characters (<,>,",',&) into XML entity references.
 *
 * @param {String} text The string within which to replace localization tokens.
 * @type String
 * @returns A string with every XML reserved character replaced with it's
 *         corresponding XML entity reference.
 */
Translator.prototype.convertXMLEntityChars = function(text) {
	text = text.replace(/&/g, "&amp;");
	text = text.replace(/</g, "&lt;");
	text = text.replace(/>/g, "&gt;");
	text = text.replace(/\"/g, "&quot;");
	text = text.replace(/\'/g, "&apos;");

	return text;
};

/**
 * The <code>getMessage</code> method replaces a token with it's translation.
 * Additionally, you can supply extra template arguments that the particular
 * translation might need. For example, a given translations may be
 * ${dialog.message.amountWarning} = "you have [template.key.amount] dollars
 * left in account [template.key.account]". You would call
 * <code>br.i18n("dialog.message.amountWarning",
 * {"template.key.amount":"43234", "template.key.account":"testAccount"});</code>
 * to get the fully translated message "you have 43234 dollars left in account
 * testAccount"
 *
 * @param {String} token The token to be translated.
 * @param {Map} templateArgs The *optional* template arguments a translation
 *            may require.
 * @type String
 * @returns A string with message tokens replaced with the current locale's
 *         messages, possibly with additional substitutions for any template
 *         arguments.
 */
Translator.prototype.getMessage = function(token, templateArgs) {
	templateArgs = templateArgs || {};
	var text = this._getTranslationForKeyOrUndefinedIfKeyIsUnknown(token);
	if (text != null) {
		for (var key in templateArgs) {
			var regEx = new RegExp("\\[" + key + "\\]", "g");
			text = text.replace(regEx, templateArgs[key]);
		}
	}
	return formatTranslationResponseIfTranslationWasUnknown(token, text);
};


/**
 * Returns the current date format string for use in displaying the current date format or for
 * other components that require it to format dates.
 *
 * The string is either the default for the locale or if the user has
 * set a preference then that is returned instead.
 *
 * @type String
 * @returns The date format string, e.g. YYYY-mm-dd.
 */
Translator.prototype.getDateFormat = function() {
	return this.localizationPrefs.dateFormat || this._getTranslationForKey("br.i18n.date.format");
};

/**
 * Returns the shorter version of the current date format string for use in displaying the current date format or for
 * other components that require it to format dates.
 *
 * The string is either the default for the locale or if the user has
 * set a preference then that is returned instead.
 *
 * @type String
 * @returns The date format string, e.g. d/m/Y.
 */
Translator.prototype.getShortDateFormat = function() {
	return this.localizationPrefs.shortDateFormat || this._getTranslationForKey("br.i18n.date.format.typed");
};

/**
 * Formats a JavaScript date object according to the locale date format
 * string or another passed in date format string. If no date format string is
 * supplied, this function will default to the date format string referenced by
 * the localization property <code>br.i18n.date.format</code>.
 *
 * <p>Try using the following:</p>
 * <pre>
 * var oTranslator = Translator.getTranslator();
 * oTranslator.formatDate(myDateObject);
 * </pre>
 *
 * <p>Note that this method will also translate any month names
 * (including abbreviated month names) in the date string to the local equivalents.
 * In order for this translation to work correctly, two sets of localization
 * properties need to be set-up.</p>
 *
 * <p>For translation of long month names define localization properties of the
 * form:
 * date.month.january=January<br/>
 *
 * For translation of abbreviated month names define localization properties of
 * the form:
 * date.month.short.january=Jan</p>
 *
 * @param {Date} date A Date object to output as a formatted string.
 * @param {String} dateFormat An optional date format to use. The date formats
 *               supported are the same as those used by the Moment.js Date object.
 *               Refer to the Moment.js API documentation for further details.
 * @type String
 * @returns The formatted date string.
 */
Translator.prototype.formatDate = function(date, dateFormat) {
	if (!dateFormat) {
		dateFormat = this.getDateFormat();
	}

	var localisedDate = new (require('./LocalisedDate'))(date);
	return localisedDate.format(dateFormat);
};

/**
 * Formats the time appropriately for the locale.
 *
 * <p>By specifying a time separator character (':' for example) as the value
 * of the localization property <code>br.i18n.time.format.separator</code>, a time such
 * as '102001' will be formatted as '10:20:01'.</p>
 *
 * <p>Try using the following:</p>
 * <pre>
 * var oTranslator = Translator.getTranslator();
 * oTranslator.formatTime(102001);
 * </pre>
 *
 * @throws {caplin.core.Error} A LocalisedTime object could not be
 *         instantiated from: <code>vTime</code>.
 * @param {Variant} time An integer or string representing the time.
 * @returns A formatted time string.
 *
 * @type String
 */
Translator.prototype.formatTime = function(time) {
	var localisedTime = new (require('./LocalisedTime'))(time);
	return localisedTime.format();
};

/**
 * Formats the number appropriately for the locale.
 *
 * <p>By specifying a number grouping separator character (',' for example) as
 * the value of the localization property <code>br.i18n.number.grouping.separator</code>,
 * a number such as '1000000' will be formatted as '1,000,000'.</p>
 *
 * <p>Try using the following:</p>
 * <pre>
 * var oTranslator = Translator.getTranslator();
 * oTranslator.formatNumber(1000000);
 * </pre>
 *
 * @throws {caplin.core.Error} A LocalisedNumber object could not be
 *         instantiated from: <code>vNumber</code>.
 * @param {Variant} number A number or a string representing the number.
 * @returns A formatted string representation of the number.
 *
 * @type String
 */
Translator.prototype.formatNumber = function(number, thousandsSeparator) {
	var localisedNumber = new LocalisedNumber(number);
	if (!thousandsSeparator) {
		thousandsSeparator = this.localizationPrefs.thousandsSeparator ||
				this._getTranslationForKey("br.i18n.number.grouping.separator");
	}
	var decimalRadixCharacter = this.localizationPrefs.decimalRadixCharacter ||
			this._getTranslationForKey("br.i18n.decimal.radix.character");

	return localisedNumber.format(thousandsSeparator, decimalRadixCharacter);
};

/**
 * Parses the number appropriately for the locale, by removing the ThousandsSeperators
 * and decimal points.
 *
 * <p>By specifying a number grouping separator character (',' for example) as
 * the value of the localization property <code>br.i18n.number.grouping.separator</code>,
 * a number such as '1,000,000' will be parsed as '1000000'.</p>
 *
 * <p>Try using the following:</p>
 * <pre>
 * var oTranslator = Translator.getTranslator();
 * oTranslator.parseNumber(1,000,000.00);
 * </pre>
 *
 * @param {Variant} number A a string representing the number.
 * @returns A parsed number or null if the value can't be parsed.
 *
 * @type Number
 */
Translator.prototype.parseNumber = function(number, thousandsSeparator) {
	if (!thousandsSeparator) {
		thousandsSeparator = this.localizationPrefs.thousandsSeparator ||
				this._getTranslationForKey("br.i18n.number.grouping.separator");
	}

	var decimalPlaceCharacter = this.localizationPrefs.decimalRadixCharacter ||
			this._getTranslationForKey("br.i18n.decimal.radix.character");

	thousandsSeparator = thousandsSeparator.replace(/[-[\]*+?.,\\^$|#\s]/g, "\\$&");
	var regEx = new RegExp(thousandsSeparator, "g");
	number = number.replace(regEx, '');
	number = number.replace(decimalPlaceCharacter, '.');

	return Number(number);
};

/**
 * Strings non numeric characters from the specified string.
 *
 * @param {String} value the string to strip the non numeric values from.
 *
 * @returns The string without numeric characters
 * @type String
 */
Translator.prototype.stripNonNumericCharacters = function(value) {
	var length = value.length;
	var joiner = [];
	var isDecimalPointFound = false;
	var decimalPlaceCharacter = this.localizationPrefs.decimalRadixCharacter || this._getTranslationForKey("br.i18n.decimal.radix.character");

	for (var i = 0; i < length; i++) {
		var thisChar = value.charAt(i);
		if (isNaN(thisChar) === true) {
			if (thisChar === decimalPlaceCharacter) {
				if (isDecimalPointFound == false) {
					joiner.push(".");
					isDecimalPointFound = true;
				}
			}
		} else {
			joiner.push(thisChar);
		}
	}
	return joiner.join("");
};

/**
 * Sets localization preferences for the <code>Translator</code>.
 *
 * @param {Map} localizationPrefs A map containing the localization preferences.
 */
Translator.prototype.setLocalizationPreferences = function(localizationPrefs) {
	this.localizationPrefs = localizationPrefs;
};

/**
 * @private
 */
Translator.prototype._getTranslationForKey = function(token) {
	var text = this._getTranslationForKeyOrUndefinedIfKeyIsUnknown(token);
	return formatTranslationResponseIfTranslationWasUnknown(token, text);
};

/**
 * @private
 */
Translator.prototype._getTranslationForKeyOrUndefinedIfKeyIsUnknown = function(token) {
	token = token.toLowerCase();
	if (this.testMode === true) {
		if (token == "br.i18n.date.format") {
			return TEST_DATE_FORMAT_SHORT;
		} else if (token == "br.i18n.date.format.long") {
			return TEST_DATE_FORMAT_LONG;
		}
		return ".";
	}
	return this.messages[token];
};

function formatTranslationResponseIfTranslationWasUnknown(key, text) {
	return (text) ? text : "??? " + key + " ???";
}

module.exports = Translator;
