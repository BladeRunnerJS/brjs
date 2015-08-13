'use strict';

var Fixture = require('br/test/Fixture');
var Core = require('br/Core');

function RequestUrlFixture() {
	this.m_oXhrFactory = null;

	this.m_pCannedResponses = [];
}

Core.inherit(RequestUrlFixture, Fixture);

RequestUrlFixture.prototype.setXhrFactory = function(oXhrFactory) {
	this.m_oXhrFactory = oXhrFactory;
};

RequestUrlFixture.prototype.addCannedResponse = function(sName, sValue) {
	this.m_pCannedResponses['<' + sName + '>'] = sValue;
};

RequestUrlFixture.prototype.canHandleExactMatch = function() {
	return false;
};

RequestUrlFixture.prototype.canHandleProperty = function(sProperty) {
	return ((sProperty == 'requestSent') || (sProperty == 'responseReceived') || (sProperty == 'noMoreRequests'));
};

RequestUrlFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue) {
	if (sPropertyName == 'responseReceived') {
		var response = '';
		if (this.m_pCannedResponses[vValue]) {
			response = this.m_pCannedResponses[vValue];
		} else {
			response = vValue;
		}

		var oXhrStub = this.m_oXhrFactory.popResponseXhr();
		oXhrStub.injectResponse(response);
		if (oXhrStub.onreadystatechange) {
			oXhrStub.onreadystatechange();
		}
	} else {
		throw new Error('Unknown property ' + sPropertyName);
	}
};
RequestUrlFixture.prototype.doGiven = RequestUrlFixture.prototype._doGivenAndDoWhen;
RequestUrlFixture.prototype.doWhen = RequestUrlFixture.prototype._doGivenAndDoWhen;

RequestUrlFixture.prototype.doThen = function(sPropertyName, vValue) {
	switch (sPropertyName) {
		case 'requestSent':
			var oXhrStub = this.m_oXhrFactory.shiftRequestXhr();
			if (!oXhrStub) {
				fail(vValue + " hadn't been requested");
			}
			assertEquals(vValue, oXhrStub.getRequestSummary());
			break;

		case 'noMoreRequests':
			var responsesLeft = new Array();
			for (i = 0; i < this.m_oXhrFactory.m_pXhrRequestQueue.length; i++) {
				responsesLeft[i] = this.m_oXhrFactory.m_pXhrRequestQueue[i].getRequestSummary();
			}

			var responsesString = (responsesLeft.length == 0) ? '' : responsesLeft.join(' ');
			assertEquals('there were requests sent which were not asserted in the test: ' + responsesString,
				vValue, this.m_oXhrFactory.m_pXhrRequestQueue.length == 0);
			break;

		default:
			throw new Error(sPropertyName + ' is not a valid request property');
			break;
	}
};

module.exports = RequestUrlFixture;

