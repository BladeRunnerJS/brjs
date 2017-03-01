'use strict';

/**
 * @module br/services/LocaleProvider
 */

/**
 * Locale providers determine which of an app's supported locales will be the active locale. How this is determined is
 * left to the locale provider, but locale providers are expected to allow the locale to be overriden by the user, via
 * the {#setActiveLocale} method, in which case {#getActiveLocale} should then continue to use the overriden locale.
 */
function LocaleProvider() {
}

/**
 * Get the active locale, as determined automatically by the service.
 */
LocaleProvider.prototype.getActiveLocale = function() {
};

/**
 * Forcefully override the automatically determined locale.
 *
 * @param {string} locale - The name of the new active locale.
 * @throws If the given locale isn't supported by the app.
 */
LocaleProvider.prototype.setActiveLocale = function(locale) {
};

module.exports = LocaleProvider;
