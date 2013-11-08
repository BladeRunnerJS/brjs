define('br/i18n', function(require, exports, module) {
	"use strict";
	
	var Errors = require('br/Errors');
	var Translator = require('br/i18n/Translator');
	var translator = undefined;
	
	function mergeMaps(arrayOfMaps) {
		return arrayOfMaps.reduce(function(accumulator, value) {
			for (var key in value) {
				accumulator[key] = value[key];
			}
			return accumulator;
		}, {});
	}

	/**
	 * Returns an instance of the <code>Translator</code>.
	 *
	 * @type Translator
	 * @returns The <code>Translator</code> for i18n translations for use within this
	 *         instance of the application.
	 */
	function getTranslator() {
		if (translator === undefined) {
			throw new Errors.IllegalStateError("i18n has not been initialized.");
		}
		return translator;
	}

	function i18n(thingToFormat, mTemplateArgs) {
		return getTranslator().getMessage(thingToFormat, mTemplateArgs);
	}
	
	i18n.reset = function() {
		translator = undefined;
	};
	
	i18n.initialise = function(i18nData) {
		if (translator !== undefined) {
			throw new Errors.IllegalStateError("i18n has already been initialized.");
		}
		translator = new Translator(mergeMaps(i18nData));
	};
	
	i18n.number = function(thingToFormat) {
		return getTranslator().formatNumber(thingToFormat);
	};
	
	i18n.date = function(thingToFormat) {
		return getTranslator().formatDate(thingToFormat);
	};
	
	i18n.time = function(thingToFormat) {
		return getTranslator().formatTime(thingToFormat);
	};
	
	i18n.getTranslator = getTranslator;
	
	module.exports = i18n;
});