'use strict';

var Utility = require('br/util/Utility');

function WindowOpenerService() {
}

WindowOpenerService.prototype.openWindow = function(sUrl) {
	Utility.interfaceMethod('WindowOpenerService', 'openWindow');
};

module.exports = WindowOpenerService;
