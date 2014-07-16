brjs.dashboard.app.service.url.PageUrlProviderStub = function(sRootUrl)
{
	// call super constructor
	brjs.dashboard.app.service.url.PageUrlProvider.call(this);
	
	this.m_sRootUrl = sRootUrl;
};
br.Core.inherit(brjs.dashboard.app.service.url.PageUrlProviderStub, brjs.dashboard.app.service.url.PageUrlProvider);

brjs.dashboard.app.service.url.PageUrlProviderStub.prototype.getRootUrl = function()
{
	return this.m_sRootUrl;
};

brjs.dashboard.app.service.url.PageUrlProviderStub.prototype.setPageUrl = function(sPageUrl)
{
	this._updatePageUrl(sPageUrl);
};
