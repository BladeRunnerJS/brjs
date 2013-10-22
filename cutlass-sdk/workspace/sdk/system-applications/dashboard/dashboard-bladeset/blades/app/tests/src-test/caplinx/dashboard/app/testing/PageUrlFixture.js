caplinx.dashboard.app.testing.PageUrlFixture = function()
{
	this.m_oPageUrlService = null;  
};
caplin.implement(caplinx.dashboard.app.testing.PageUrlFixture, caplin.testing.Fixture);

caplinx.dashboard.app.testing.PageUrlFixture.prototype.setPageUrlService = function(oPageUrlService) {
	this.m_oPageUrlService = oPageUrlService;
};

caplinx.dashboard.app.testing.PageUrlFixture.prototype.canHandleExactMatch = function() 
{
	return false;
};

caplinx.dashboard.app.testing.PageUrlFixture.prototype.canHandleProperty = function(sProperty) 
{
	 return sProperty == "url";
};

caplinx.dashboard.app.testing.PageUrlFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue)
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
caplinx.dashboard.app.testing.PageUrlFixture.prototype.doGiven = caplinx.dashboard.app.testing.PageUrlFixture.prototype._doGivenAndDoWhen;
caplinx.dashboard.app.testing.PageUrlFixture.prototype.doWhen = caplinx.dashboard.app.testing.PageUrlFixture.prototype._doGivenAndDoWhen;

caplinx.dashboard.app.testing.PageUrlFixture.prototype.doThen = function(sPropertyName, vValue)
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
