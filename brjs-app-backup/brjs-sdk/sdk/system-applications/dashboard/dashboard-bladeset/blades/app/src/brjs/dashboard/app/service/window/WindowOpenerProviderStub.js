'use strict';

var Core = require('br/Core');
var WindowOpenerService = require("brjs/dashboard/app/service/window/WindowOpenerService");

function WindowOpenerProviderStub() {
	this.m_pOpenerRequests = [];
}

Core.inherit(WindowOpenerProviderStub, WindowOpenerService);

WindowOpenerProviderStub.prototype.openWindow = function(sUrl) {
	this.m_pOpenerRequests.push(sUrl);
};

WindowOpenerProviderStub.prototype.getOpenerRequests = function() {
	return this.m_pOpenerRequests;
};

module.exports = WindowOpenerProviderStub;
