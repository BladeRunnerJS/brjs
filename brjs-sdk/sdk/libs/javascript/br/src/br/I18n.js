"use strict";

/**
* Returns a {@link module:br/i18n/I18N} object that provides access to i18n functions.
* @module br/i18n
* @exports module:br/i18n/I18N
*/

var Translator = require('br/i18n/Translator');
var I18N = require('br/i18n/I18N');

module.exports = I18N.create(new Translator(window._brjsI18nProperties || {}, window._brjsI18nUseLocale));
