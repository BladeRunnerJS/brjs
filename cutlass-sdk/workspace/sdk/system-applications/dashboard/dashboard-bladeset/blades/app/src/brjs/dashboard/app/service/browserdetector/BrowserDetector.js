br.Core.thirdparty("jquery-browser");

brjs.dashboard.app.service.browserdetector.BrowserDetector = function()
{

}

brjs.dashboard.app.service.browserdetector.BrowserDetector.prototype.getBrowserName = function()
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

brjs.dashboard.app.service.browserdetector.BrowserDetector.prototype.getBrowserVersion = function()
{
	if (jQuery.browser.versionNumber) 
	{
		return jQuery.browser.versionNumber;
	}
	return "";
}

