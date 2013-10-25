caplinx.dashboard.app.service.browserdetector.InvalidBrowserDecider  = function(oBrowserDetector)
{
	this.m_oBrowserDetector = oBrowserDetector;
	
	this.m_pMinimumBrowserVersions = {
		ie : 9,
		chrome : 5,
		firefox : 3.5,
		safari : 5
	}
}

caplinx.dashboard.app.service.browserdetector.InvalidBrowserDecider.prototype.isValidBrowser = function(oBrowserDetector)
{
	var sBrowserName = this.m_oBrowserDetector.getBrowserName();
	var sBrowserVersion = this.m_oBrowserDetector.getBrowserVersion();
	
	var vMinimumBrowserVersion = this.m_pMinimumBrowserVersions[sBrowserName];
	if (sBrowserName == "" || sBrowserVersion == "" || vMinimumBrowserVersion === undefined)
	{
		return true;
	}
	
	return sBrowserVersion >= vMinimumBrowserVersion;
		
}

caplinx.dashboard.app.service.browserdetector.InvalidBrowserDecider.prototype.getMinimumBrowserVersions = function()
{
	return this.m_pMinimumBrowserVersions;
}