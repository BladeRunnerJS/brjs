br.thirdparty("jquery-browser");

caplinx.dashboard.app.service.browserdetector.BrowserDetector = function()
{

}

caplinx.dashboard.app.service.browserdetector.BrowserDetector.prototype.getBrowserName = function()
{
	if (jQuery.browser.name) 
	{
		var sBrowser = jQuery.browser.name;
		if (sBrowser == "msie") 
		{
			return "ie";
		}
		return sBrowser;
	}
	return "";
}

caplinx.dashboard.app.service.browserdetector.BrowserDetector.prototype.getBrowserVersion = function()
{
	if (jQuery.browser.versionNumber) 
	{
		return jQuery.browser.versionNumber;
	}
	return "";
}

