brjs.dashboard.app.service.url.PageUrlProvider = function()
{
	this.m_sPageUrl = window.location.hash;
	this.m_nNextListenerId = 0;
	this.m_mListeners = {};
	
	var oThis = this;
	if (window.addEventListener)
	{
		window.addEventListener("hashchange", function() {
			oThis._updatePageUrl(window.location.hash);
		}, false);
	}
};
br.Core.inherit(brjs.dashboard.app.service.url.PageUrlProvider, brjs.dashboard.app.service.url.PageUrlService);

brjs.dashboard.app.service.url.PageUrlProvider.prototype.getRootUrl = function()
{
	return window.location.href.replace(/dashboard\/[a-z]{2}(_[A-Z]{2})?\/#apps.*$/, "");
};

brjs.dashboard.app.service.url.PageUrlProvider.prototype.getPageUrl = function()
{
	return this.m_sPageUrl;
};


brjs.dashboard.app.service.url.PageUrlProvider.prototype.addPageUrlListener = function(fListener, bProvideInitialValue)
{
	this.m_mListeners[this.m_nNextListenerId++] = fListener;
	
	if(bProvideInitialValue)
	{
		fListener(this.m_sPageUrl);
	}
};

brjs.dashboard.app.service.url.PageUrlProvider.prototype.removePageUrlListener = function(sListenerId)
{
	delete this.m_mListeners[sListenerId];
};

brjs.dashboard.app.service.url.PageUrlProvider.prototype._updatePageUrl = function(sPageUrl)
{
	//TODO: uncomment these lines - the URL doesnt need to be updated if it is the same as the current URL
	//if (sPageUrl != this.m_sPageUrl) 
	//{
		this.m_sPageUrl = sPageUrl;
		
		for(var sListenerId in this.m_mListeners)
		{
			var fListener = this.m_mListeners[sListenerId];
			fListener(sPageUrl);
		}
	//}
};
