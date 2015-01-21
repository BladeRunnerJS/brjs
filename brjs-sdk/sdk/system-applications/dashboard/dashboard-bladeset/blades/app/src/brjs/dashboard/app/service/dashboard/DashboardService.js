brjs.dashboard.app.service.dashboard.DashboardService = function()
{
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.getWarUrl = function(sApp)
{
	br.util.Utility.interfaceMethod("DashboardService", "getWarUrl");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.getAppImageUrl = function(sApp)
{
	br.util.Utility.interfaceMethod("DashboardService", "getAppImageUrl");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.getTestResultsUrl = function()
{
	br.util.Utility.interfaceMethod("DashboardService", "getTestResultsUrl");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.getApps = function(fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "getApps");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.getApp = function(sApp, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "getApp");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.importMotif = function(sNewApp, sNamespace, sFileInputElemId, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "importMotif");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.importBlades = function(sSourceApp, mBlades, sTargetApp, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "importBlades");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.createApp = function(sApp, sNamespace, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "createApp");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.createBladeset = function(sApp, sBladeset, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "createBladeset");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.createBlade = function(sApp, sBladeset, sBlade, fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "createBlade");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.runBladesetTests = function(sApp, sBladeset, fProgressListener, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "runBladesetTests");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.runBladeTests = function(sApp, sBladeset, sBlade, fProgressListener, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "runBladeTests");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.getCurrentReleaseNote = function(fCallback, fErrorCallback)
{
	br.util.Utility.interfaceMethod("DashboardService", "getCurrentReleaseNote");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.getTestRunInProgress = function()
{
	br.util.Utility.interfaceMethod("DashboardService", "getTestRunInProgress");
};

brjs.dashboard.app.service.dashboard.DashboardService.prototype.setTestRunInProgress = function(bValue)
{
	br.util.Utility.interfaceMethod("DashboardService", "setTestRunInProgress");
};