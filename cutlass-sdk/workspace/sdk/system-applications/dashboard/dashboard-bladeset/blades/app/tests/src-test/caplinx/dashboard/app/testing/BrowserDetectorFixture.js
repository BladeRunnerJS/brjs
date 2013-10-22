caplinx.dashboard.app.testing.BrowserDetectorFixture = function()
{
	this.m_oBrowserDetector = null;
};
caplin.implement(caplinx.dashboard.app.testing.BrowserDetectorFixture, caplin.testing.Fixture);

caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype.setBrowserDetector = function(oBrowserDetector) 
{
	this.m_oBrowserDetector = oBrowserDetector;
};

caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype.canHandleExactMatch = function() 
{
	return false;
};

caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype.canHandleProperty = function(sProperty) 
{
	 return sProperty == "name" || sProperty == "version";
};

caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue)
{
	if (sPropertyName == "name")
	{
		this.m_oBrowserDetector.browserName = vValue;
	}
	else if (sPropertyName == "version")
	{
		this.m_oBrowserDetector.browserVersion = vValue;
	}
	else
	{
		fail("Unknown property " + sPropertyName);
	}
};
caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype.doGiven = caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype._doGivenAndDoWhen;
caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype.doWhen = caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype._doGivenAndDoWhen;

caplinx.dashboard.app.testing.BrowserDetectorFixture.prototype.doThen = function(sPropertyName, vValue)
{
	if (sPropertyName == "name")
	{
		assertEquals(vValue, this.m_oBrowserDetector.getBrowserName());
	}
	else if (sPropertyName == "version")
	{
		assertEquals(vValue, this.m_oBrowserDetector.getBrowserVersion());
	}
	else
	{
		fail("Unknown property " + sPropertyName);
	}
};

