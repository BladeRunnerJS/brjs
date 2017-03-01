'use strict';

var Fixture = require('br/test/Fixture');
var Core = require('br/Core');

function BrowserDetectorFixture() {
	this.m_oBrowserDetector = null;
}

Core.inherit(BrowserDetectorFixture, Fixture);

BrowserDetectorFixture.prototype.setBrowserDetector = function(oBrowserDetector) {
	this.m_oBrowserDetector = oBrowserDetector;
};

BrowserDetectorFixture.prototype.canHandleExactMatch = function() {
	return false;
};

BrowserDetectorFixture.prototype.canHandleProperty = function(sProperty) {
	return sProperty == 'name' || sProperty == 'version';
};

BrowserDetectorFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue) {
	if (sPropertyName == 'name') {
		this.m_oBrowserDetector.browserName = vValue;
	} else if (sPropertyName == 'version') {
		this.m_oBrowserDetector.browserVersion = vValue;
	} else {
		fail('Unknown property ' + sPropertyName);
	}
};
BrowserDetectorFixture.prototype.doGiven = BrowserDetectorFixture.prototype._doGivenAndDoWhen;
BrowserDetectorFixture.prototype.doWhen = BrowserDetectorFixture.prototype._doGivenAndDoWhen;

BrowserDetectorFixture.prototype.doThen = function(sPropertyName, vValue) {
	if (sPropertyName == 'name') {
		assertEquals(vValue, this.m_oBrowserDetector.getBrowserName());
	} else if (sPropertyName == 'version') {
		assertEquals(vValue, this.m_oBrowserDetector.getBrowserVersion());
	} else {
		fail('Unknown property ' + sPropertyName);
	}
};

module.exports = BrowserDetectorFixture;

