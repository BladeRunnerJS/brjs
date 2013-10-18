caplinx.dashboard.app.service.dashboard.DashboardService = function()
{
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.getWarUrl = function(sApp)
{
	br.util.Utility.interfaceMethod("DashboardService", "getWarUrl");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.getAppImageUrl = function(sApp)
{
	br.util.Utility.interfaceMethod("DashboardService", "getAppImageUrl");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.getTestResultsUrl = function()
{
	br.util.Utility.interfaceMethod("DashboardService", "getTestResultsUrl");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.getApps = function(fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "getApps");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.getApp = function(sApp, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "getApp");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.importMotif = function(sNewApp, sNamespace, sFileInputElemId, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "importMotif");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.resetDatabase = function(sApp, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "resetDatabase");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.importBlades = function(sSourceApp, mBlades, sTargetApp, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "importBlades");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.createApp = function(sApp, sNamespace, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "createApp");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.createBladeset = function(sApp, sBladeset, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "createBladeset");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.createBlade = function(sApp, sBladeset, sBlade, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "createBlade");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.runBladesetTests = function(sApp, sBladeset, fProgressListener, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "runBladesetTests");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.runBladeTests = function(sApp, sBladeset, sBlade, fProgressListener, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "runBladeTests");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.getCurrentReleaseNote = function(fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "getCurrentReleaseNote");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.getTestRunInProgress = function()
{
	br.util.Utility.interfaceMethod("DashboardService", "getTestRunInProgress");
};

caplinx.dashboard.app.service.dashboard.DashboardService.prototype.setTestRunInProgress = function(bValue)
{
	br.util.Utility.interfaceMethod("DashboardService", "setTestRunInProgress");
};