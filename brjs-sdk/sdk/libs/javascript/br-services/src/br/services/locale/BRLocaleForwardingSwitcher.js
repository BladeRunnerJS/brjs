'use strict';

var topiarist = require('topiarist');
var LocaleSwitcher = require('br/services/LocaleSwitcher');

/**
 * @module br/services/locale/BRLocaleForwardingSwitcher
 */

function BRLocaleForwardingSwitcher() {
}
topiarist.implement(BRLocaleForwardingSwitcher, LocaleSwitcher);

BRLocaleForwardingSwitcher.prototype.switch = function(localePageUrl) {
	window.location = localePageUrl;
};

module.exports = BRLocaleForwardingSwitcher;
