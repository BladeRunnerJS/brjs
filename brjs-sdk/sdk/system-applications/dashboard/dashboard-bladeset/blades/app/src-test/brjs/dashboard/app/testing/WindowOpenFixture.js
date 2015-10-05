'use strict';

var Fixture = require('br/test/Fixture');
var Core = require('br/Core');

function WindowOpenFixture() {
	this.bIgnoreEvents = false;
}

Core.inherit(WindowOpenFixture, Fixture);

WindowOpenFixture.prototype.setWindowOpenerService = function(oWindowOpenerService) {
	this.m_oWindowOpenerService = oWindowOpenerService;
};

WindowOpenFixture.prototype.tearDown = function() {
	if (!this.bIgnoreEvents) {
		assertEquals('window.open function calls were made which were not expected in the test',
			0, this.m_oWindowOpenerService.getOpenerRequests().length);
	}
};

WindowOpenFixture.prototype.doGiven = function(sPropertyName, vValue) {
	if (sPropertyName == 'ignoreEvents') {
		this.bIgnoreEvents = vValue;
	} else {
		fail('given not supported for ' + sPropertyName);
	}
};

WindowOpenFixture.prototype.doWhen = function(sPropertyName, vValue) {
	fail('when is not supported by WindowOpenFixture');
};

WindowOpenFixture.prototype.doThen = function(sPropertyName, vValue) {
	assertTrue('no window.open function calls were triggered', this.m_oWindowOpenerService.getOpenerRequests().length >= 1);

	var sRequest = this.m_oWindowOpenerService.getOpenerRequests().shift();
	assertEquals('window.open was invoked with the wrong url', vValue, sRequest);
};

WindowOpenFixture.prototype.addSubFixtures = function(oFixtureRegistry) {};

WindowOpenFixture.prototype.canHandleExactMatch = function() {
	return true;
};

WindowOpenFixture.prototype.canHandleProperty = function(sProperty) {
	return sProperty == 'ignoreEvents';
};

WindowOpenFixture.prototype._getOpenerRequests = function() {
	return this.m_oWindowOpenerService.getOpenerRequests().length;
};

module.exports = WindowOpenFixture;
