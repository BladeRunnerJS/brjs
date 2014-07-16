"use strict";

/**
* Provides i18n functions. Accessed via the [br/i18n]{@link module:br/i18n} class.
* @module br/i18n/I18N
*/
var I18N = {};

/** @private */
I18N.create = function(translator) {

	/**
	* Returns the translation for a message using the current locale.
	* @name i18n
	* @method
	* @static
	* @param {String} thingToFormat The translation key.
	* @param {String} [mTemplateArgs] Arguments provided by the template
	* @see {@link br/i18n/Translator:#getMessage}
	*/
	var i18n = function(thingToFormat, mTemplateArgs) {
		return translator.getMessage(thingToFormat, mTemplateArgs);
	};

	/**
	* Returns a number formatted according to the current locale settings.
	* @name number
	* @method
	* @static
	* @param {String} thingToFormat The number to format
	* @see {@link br/i18n/Translator:#formatNumber}
	*/
	i18n.number = function(thingToFormat) {
		return translator.formatNumber(thingToFormat);
	};

	/**
	* Returns a data formatted according to the current locale settings.
	* @name date
	* @method
	* @static
	* @param {String} thingToFormat The data to format
	* @see {@link br/i18n/Translator:#formatDate}
	*/
	i18n.date = function(thingToFormat) {
		return translator.formatDate(thingToFormat);
	};

	/**
	* Returns a time formatted according to the current locale settings.
	* @name time
	* @method
	* @static
	* @param {String} thingToFormat The time to format
	* @see {@link br/i18n/Translator:#formatTime}
	*/
	i18n.time = function(thingToFormat) {
		return translator.formatTime(thingToFormat);
	};

	/**
	* Returns the [Translator]{@link module:br/i18n/Translator} used.
	* @name getTranslator
	* @method
	* @static
	*/
	i18n.getTranslator = function() {
		return translator;
	};

	return i18n;
};

module.exports = I18N;
