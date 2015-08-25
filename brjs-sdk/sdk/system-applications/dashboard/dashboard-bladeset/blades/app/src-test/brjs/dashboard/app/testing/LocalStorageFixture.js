'use strict';

var Fixture = require('br/test/Fixture');
var Core = require('br/Core');

function LocalStorageFixture() {
	this.m_oLocalStorage = null;
}

Core.inherit(LocalStorageFixture, Fixture);

LocalStorageFixture.prototype.setLocalStorage = function(oLocalStorage) {
	this.m_oLocalStorage = oLocalStorage;
};

LocalStorageFixture.prototype.canHandleExactMatch = function() {
	return false;
};

LocalStorageFixture.prototype.canHandleProperty = function(sProperty) {
	return true;
};

LocalStorageFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue) {
	this.m_oLocalStorage.setItem(sPropertyName, vValue);
};
LocalStorageFixture.prototype.doGiven = LocalStorageFixture.prototype._doGivenAndDoWhen;
LocalStorageFixture.prototype.doWhen = LocalStorageFixture.prototype._doGivenAndDoWhen;

LocalStorageFixture.prototype.doThen = function(sPropertyName, vValue) {
	assertEquals(vValue, this.m_oLocalStorage.getItem(sPropertyName));
};

module.exports = LocalStorageFixture;

