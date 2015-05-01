'use strict';

var topiarist = require('topiarist');
var LocaleSwitcher = require('br/services/LocaleSwitcher');

/**
 * @module br/services/locale/BRLocaleForwardingSwitcher
 */

/**
 * The forwarding locale-switcher is the default implementation of {br/services/LocaleSwitcher} in BRJS.
 *
 * It works by forwarding the browser to a locale specific URL (e.g. 'app/en'). Some static file servers are unable to
 * serve apps that use this locale-switcher as it relies on the server being able to infer the file suffix
 * (e.g. '.html') of the locale specific index page.
 */
function BRLocaleForwardingSwitcher() {
}
topiarist.implement(BRLocaleForwardingSwitcher, LocaleSwitcher);

BRLocaleForwardingSwitcher.prototype.switchLocale = function(localePageUrl) {
	window.location = localePageUrl;
};

module.exports = BRLocaleForwardingSwitcher;
