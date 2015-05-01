'use strict';

/**
 * @module br/services/LocaleSwitcher
 */

/**
 * Locale switchers are responsible for ensuring that the browser loads the app using the active locale (as determined
 * using {br/services/LocaleSwitcher#getActiveLocale}) when directed to do so.
 *
 * There are two standard implementations provided with BladeRunnerJS:
 *
 * <ul>
 *  <li>{br/services/locale/BRLocaleForwardingSwitcher}: Causes the browser to load the locale specific page using a URL
 *   that includes the locale.</li>
 *  <li>{br/services/locale/BRLocaleLoadingSwitcher}: Causes the browser to replace the contents of the locale switching
 *   page with the contents of the locale specific page.</li>
 * </ul>
 */
function LocaleSwitcher() {
}

/**
 * Requests the locale switcher to switch to whatever is the active locale.
 *
 * @param {string} localePageUrl - The URL of the locale specific web page.
 */
LocaleSwitcher.prototype.switchLocale = function(localePageUrl) {
};

module.exports = LocaleSwitcher;
