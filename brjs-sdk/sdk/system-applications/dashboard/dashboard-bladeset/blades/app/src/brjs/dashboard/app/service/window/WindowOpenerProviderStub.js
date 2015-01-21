brjs.dashboard.app.service.window.WindowOpenerProviderStub = function()
{
	this.m_pOpenerRequests = [];
};
br.Core.inherit(brjs.dashboard.app.service.window.WindowOpenerProviderStub, brjs.dashboard.app.service.window.WindowOpenerService);

brjs.dashboard.app.service.window.WindowOpenerProviderStub.prototype.openWindow = function(sUrl)
{
	this.m_pOpenerRequests.push(sUrl);
};

brjs.dashboard.app.service.window.WindowOpenerProviderStub.prototype.getOpenerRequests = function()
{
	return this.m_pOpenerRequests;
};
