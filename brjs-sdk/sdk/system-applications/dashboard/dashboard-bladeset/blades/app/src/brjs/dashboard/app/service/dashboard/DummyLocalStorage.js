'use strict';

function DummyLocalStorage() {
	this.m_pStorage = new Array();
}

DummyLocalStorage.prototype.getItem = function(sKey) {
	return this.m_pStorage[sKey];
};

DummyLocalStorage.prototype.setItem = function(sKey, vValue) {
	this.m_pStorage[sKey] = vValue;
};

DummyLocalStorage.prototype.clearItems = function() {
	this.m_pStorage = new Array();
};

module.exports = DummyLocalStorage;
