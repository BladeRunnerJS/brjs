'use strict';

function TestXhrStub() {
	this.readyState = null;
	this.status = null;
	this.responseText = null;

	this.m_sMethod = null;
	this.m_sUrl = null;
	this.m_sRequestBody = null;
}

TestXhrStub.prototype.open = function(sMethod, sUrl) {
	this.readyState = 1;
	this.m_sMethod = sMethod;
	this.m_sUrl = sUrl;
};

TestXhrStub.prototype.send = function(sRequestBody) {
	this.readyState = 2;
	this.m_sRequestBody = sRequestBody;
};

TestXhrStub.prototype.getRequestSummary = function() {
	return this.m_sMethod + ' ' + this.m_sUrl + this._getParameters();
};

TestXhrStub.prototype.injectResponse = function(response) {
	if (response.indexOf(' ') > 0) {
		this.status = response.substring(0, response.indexOf(' '));
		this.responseText = response.substring(response.indexOf(' ') + 1);
	} else {
		this.status = response;
		this.responseText = '""';
	}

	this.readyState = 4;
};

TestXhrStub.prototype._getParameters = function() {
	if (this.m_sRequestBody !== '' & this.m_sRequestBody !== null) {
		return ' ' + JSON.stringify(JSON.parse(this.m_sRequestBody)).replace(/['"]/g, '');
	}

	return '';
};

module.exports = TestXhrStub;
