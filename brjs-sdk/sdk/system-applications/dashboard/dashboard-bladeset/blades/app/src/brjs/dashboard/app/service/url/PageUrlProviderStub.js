'use strict';

var Core = require('br/Core');
var PageUrlProvider = require("brjs/dashboard/app/service/url/PageUrlProvider");

function PageUrlProviderStub(sRootUrl) {
	// call super constructor
	PageUrlProvider.call(this);

	this.m_sRootUrl = sRootUrl;
}

Core.inherit(PageUrlProviderStub, PageUrlProvider);

PageUrlProviderStub.prototype.getRootUrl = function() {
	return this.m_sRootUrl;
};

PageUrlProviderStub.prototype.setPageUrl = function(sPageUrl) {
	this._updatePageUrl(sPageUrl);
};

module.exports = PageUrlProviderStub;
