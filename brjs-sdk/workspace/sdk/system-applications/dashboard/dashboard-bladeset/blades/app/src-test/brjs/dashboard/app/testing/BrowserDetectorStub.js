brjs.dashboard.app.testing.BrowserDetectorStub = function()
{
	this.browserName = "chrome";
	this.browserVersion = "18";
}

brjs.dashboard.app.testing.BrowserDetectorStub.prototype.getBrowserName = function()
{
	return this.browserName;
}

brjs.dashboard.app.testing.BrowserDetectorStub.prototype.getBrowserVersion = function()
{
	return this.browserVersion;
}