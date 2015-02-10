'use strict';

var topiarist = require('topiarist');
var LocaleSwitcher = require('br/services/LocaleSwitcher');

/**
 * @module br/services/locale/BRLocaleLoadingSwitcher
 */

function BRLocaleLoadingSwitcher() {
}
topiarist.implement(BRLocaleLoadingSwitcher, LocaleSwitcher);

BRLocaleLoadingSwitcher.prototype.switch = function(localePageUrl) {
  var request = new XMLHttpRequest();
  request.onreadystatechange = function () {
    if(this.readyState == 4) {
      document.write(this.responseText);
    }
  };
  
  request.open('GET', localePageUrl, true);
  request.send(null);
};

module.exports = BRLocaleLoadingSwitcher;
