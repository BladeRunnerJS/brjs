"use strict";

/**
* Returns a {@link module:br/i18n/I18N} object that provides
* access to i18n properties and functions.
* @module br/i18n
* @exports module:br/i18n/I18N
*/

var Translator = require('br/i18n/Translator');
var I18N = require('br/i18n/I18N');

// TODO: find out why we are sending data to the client in a non-optimal format
function mergeMaps(arrayOfMaps) {
	return arrayOfMaps.reduce(function(accumulator, value) {
		for (var key in value) {
			accumulator[key] = value[key];
		}
		return accumulator;
	}, {});
}

module.exports = I18N.create(new Translator(mergeMaps(window._brjsI18nProperties || [])));
