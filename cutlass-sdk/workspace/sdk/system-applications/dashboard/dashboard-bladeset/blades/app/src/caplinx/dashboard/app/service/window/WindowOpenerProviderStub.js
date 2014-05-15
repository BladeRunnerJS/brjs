caplinx.dashboard.app.service.window.WindowOpenerProviderStub = function()
{
	this.m_pOpenerRequests = [];
};
br.Core.inherit(caplinx.dashboard.app.service.window.WindowOpenerProviderStub, caplinx.dashboard.app.service.window.WindowOpenerService);

caplinx.dashboard.app.service.window.WindowOpenerProviderStub.prototype.openWindow = function(sUrl)
{
	this.m_pOpenerRequests.push(sUrl);
};

caplinx.dashboard.app.service.window.WindowOpenerProviderStub.prototype.getOpenerRequests = function()
{
	return this.m_pOpenerRequests;
};
