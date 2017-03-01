'use strict';

function BrowserDetectorStub() {
	this.browserName = 'chrome';
	this.browserVersion = '18';
}

BrowserDetectorStub.prototype.getBrowserName = function() {
	return this.browserName;
};

BrowserDetectorStub.prototype.getBrowserVersion = function() {
	return this.browserVersion;
};

module.exports = BrowserDetectorStub;
