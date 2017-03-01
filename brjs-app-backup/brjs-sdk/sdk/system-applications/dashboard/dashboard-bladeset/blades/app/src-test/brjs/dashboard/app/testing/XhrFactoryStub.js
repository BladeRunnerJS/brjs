'use strict';

var TestXhrStub = require("brjs/dashboard/app/testing/TestXhrStub");

function XhrFactoryStub() {
	this.m_pXhrRequestQueue = [];
	this.m_pXhrResponseQueue = [];
}

XhrFactoryStub.prototype.getRequestObject = function() {
	var oXhrStub = new TestXhrStub();

	this.m_pXhrRequestQueue.push(oXhrStub);
	this.m_pXhrResponseQueue.push(oXhrStub);

	return oXhrStub;
};

XhrFactoryStub.prototype.shiftRequestXhr = function() {
	return this.m_pXhrRequestQueue.shift();
};

XhrFactoryStub.prototype.popResponseXhr = function() {
	return this.m_pXhrResponseQueue.pop();
};

module.exports = XhrFactoryStub;
