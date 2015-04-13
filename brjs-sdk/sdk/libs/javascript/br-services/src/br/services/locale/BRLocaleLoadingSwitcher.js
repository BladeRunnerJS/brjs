'use strict';

var topiarist = require('topiarist');
var LocaleSwitcher = require('br/services/LocaleSwitcher');

/**
 * @module br/services/locale/BRLocaleLoadingSwitcher
 */

 /**
  * The loading locale-switcher is an alternative implementation of {br/services/LocaleSwitcher}.
  *
  * Unlike {br/services/locale/BRLocaleForwardingSwitcher}, it doesn't require the locale to appear in the URL, and
  * works reliably with all static file servers, but has a couple of down-sides:
  *
  * <ul>
  *   <li>The browser's view-source feature no longer displays the correct output.</li>
  *   <li>The browser may be unable to accurately display the source that is being debugged.</li>
  * </ul>
  */
function BRLocaleLoadingSwitcher() {
}
topiarist.implement(BRLocaleLoadingSwitcher, LocaleSwitcher);

BRLocaleLoadingSwitcher.prototype.switchLocale = function(localePageUrl) {
  var request = new XMLHttpRequest();
  
  request.onreadystatechange = function () {
    if(request.readyState == 4) {
      if(request.status == 200) {
        document.write(request.responseText);
      }
      else if((request.status == 404) && (!localePageUrl.match(/\.html$/))) {
        // not all web servers automatically infer the file suffix if one isn't provided, and with some basic web
        // servers this may not even be configurable, so try adding a '.html' suffix on the client -- if it's a '.jsp'
        // page than the server will either automatically do this, or can be configured to do this by modifying
        // 'WEB-INF/web.xml'.
        this.switchLocale(localePageUrl + '.html');
      }
    }
  }.bind(this);
  
  request.open('GET', localePageUrl, true);
  request.send(null);
};

module.exports = BRLocaleLoadingSwitcher;
