'use strict';

var Core = require('br/Core');
var WindowOpenerService = require("brjs/dashboard/app/service/window/WindowOpenerService");

function WindowOpenerProvider() {
}

Core.inherit(WindowOpenerProvider, WindowOpenerService);

WindowOpenerProvider.prototype.openWindow = function(sUrl) {
	window.open(sUrl, '_blank');
};

module.exports = WindowOpenerProvider;
