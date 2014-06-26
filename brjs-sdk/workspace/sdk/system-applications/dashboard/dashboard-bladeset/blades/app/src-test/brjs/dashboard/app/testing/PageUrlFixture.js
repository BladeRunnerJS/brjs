brjs.dashboard.app.testing.PageUrlFixture = function()
{
	this.m_oPageUrlService = null;  
};
br.Core.inherit(brjs.dashboard.app.testing.PageUrlFixture, br.test.Fixture);

brjs.dashboard.app.testing.PageUrlFixture.prototype.setPageUrlService = function(oPageUrlService) {
	this.m_oPageUrlService = oPageUrlService;
};

brjs.dashboard.app.testing.PageUrlFixture.prototype.canHandleExactMatch = function() 
{
	return false;
};

brjs.dashboard.app.testing.PageUrlFixture.prototype.canHandleProperty = function(sProperty) 
{
	 return sProperty == "url";
};

brjs.dashboard.app.testing.PageUrlFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue)
{
	if (sPropertyName == "url")
	{
		this.m_oPageUrlService.setPageUrl(vValue);
	}
	else
	{
		fail("Unknown property " + sPropertyName);
	}
};
brjs.dashboard.app.testing.PageUrlFixture.prototype.doGiven = brjs.dashboard.app.testing.PageUrlFixture.prototype._doGivenAndDoWhen;
brjs.dashboard.app.testing.PageUrlFixture.prototype.doWhen = brjs.dashboard.app.testing.PageUrlFixture.prototype._doGivenAndDoWhen;

brjs.dashboard.app.testing.PageUrlFixture.prototype.doThen = function(sPropertyName, vValue)
{
	if (sPropertyName == "url")
	{
		assertEquals(vValue, this.m_oPageUrlService.getPageUrl());
	}
	else
	{
		fail("Unknown property " + sPropertyName);
	}
};
