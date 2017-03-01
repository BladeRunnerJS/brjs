'use strict';

var Utility = require('br/util/Utility');

function PageUrlService() {
}

PageUrlService.prototype.getRootUrl = function() {
	Utility.interfaceMethod('PageUrlService', 'getRootUrl');
};

PageUrlService.prototype.addPageUrlListener = function(fListener, bProvideInitialValue) {
	Utility.interfaceMethod('PageUrlService', 'addPageUrlListener');
};

PageUrlService.prototype.removePageUrlListener = function(sListenerId) {
	Utility.interfaceMethod('PageUrlService', 'removePageUrlListener');
};

module.exports = PageUrlService;
