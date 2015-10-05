'use strict';

var Fixture = require('br/test/Fixture');
var Core = require('br/Core');

function PageUrlFixture() {
	this.m_oPageUrlService = null;
}

Core.inherit(PageUrlFixture, Fixture);

PageUrlFixture.prototype.setPageUrlService = function(oPageUrlService) {
	this.m_oPageUrlService = oPageUrlService;
};

PageUrlFixture.prototype.canHandleExactMatch = function() {
	return false;
};

PageUrlFixture.prototype.canHandleProperty = function(sProperty) {
	return sProperty == 'url';
};

PageUrlFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue) {
	if (sPropertyName == 'url') {
		this.m_oPageUrlService.setPageUrl(vValue);
	} else {
		fail('Unknown property ' + sPropertyName);
	}
};
PageUrlFixture.prototype.doGiven = PageUrlFixture.prototype._doGivenAndDoWhen;
PageUrlFixture.prototype.doWhen = PageUrlFixture.prototype._doGivenAndDoWhen;

PageUrlFixture.prototype.doThen = function(sPropertyName, vValue) {
	if (sPropertyName == 'url') {
		assertEquals(vValue, this.m_oPageUrlService.getPageUrl());
	} else {
		fail('Unknown property ' + sPropertyName);
	}
};

module.exports = PageUrlFixture;
