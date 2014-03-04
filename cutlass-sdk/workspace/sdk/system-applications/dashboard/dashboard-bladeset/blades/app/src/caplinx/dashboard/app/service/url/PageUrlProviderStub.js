caplinx.dashboard.app.service.url.PageUrlProviderStub = function(sRootUrl)
{
	// call super constructor
	caplinx.dashboard.app.service.url.PageUrlProvider.call(this);
	
	this.m_sRootUrl = sRootUrl;
};
br.Core.inherit(caplinx.dashboard.app.service.url.PageUrlProviderStub, caplinx.dashboard.app.service.url.PageUrlProvider);

caplinx.dashboard.app.service.url.PageUrlProviderStub.prototype.getRootUrl = function()
{
	return this.m_sRootUrl;
};

caplinx.dashboard.app.service.url.PageUrlProviderStub.prototype.setPageUrl = function(sPageUrl)
{
	this._updatePageUrl(sPageUrl);
};
