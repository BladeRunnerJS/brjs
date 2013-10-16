caplinx.dashboard.app.testing.BrowserDetectorStub = function()
{
	this.browserName = "chrome";
	this.browserVersion = "18";
}

caplinx.dashboard.app.testing.BrowserDetectorStub.prototype.getBrowserName = function()
{
	return this.browserName;
}

caplinx.dashboard.app.testing.BrowserDetectorStub.prototype.getBrowserVersion = function()
{
	return this.browserVersion;
}