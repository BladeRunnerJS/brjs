brjs.dashboard.app.service.window.WindowOpenerProvider = function()
{
};
br.Core.inherit(brjs.dashboard.app.service.window.WindowOpenerProvider, brjs.dashboard.app.service.window.WindowOpenerService);

brjs.dashboard.app.service.window.WindowOpenerProvider.prototype.openWindow = function(sUrl)
{
	window.open(sUrl, "_blank");
};
