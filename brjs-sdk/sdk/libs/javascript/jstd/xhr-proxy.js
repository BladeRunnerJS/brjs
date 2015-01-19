if(!XMLHttpRequest.prototype.origOpen)
{
	XMLHttpRequest.prototype.origOpen = XMLHttpRequest.prototype.open;
	
	XMLHttpRequest.prototype.open = function(sMethod, sResourceUrl, bAsync)
	{
		if(!sResourceUrl.match(/\/(test|query|slave)\//))
		{
			sResourceUrl = CaplinJsTestDriverPlugin.getResourcePath(sResourceUrl);
		}
		
		this.origOpen(sMethod, sResourceUrl, bAsync);
	};
}
