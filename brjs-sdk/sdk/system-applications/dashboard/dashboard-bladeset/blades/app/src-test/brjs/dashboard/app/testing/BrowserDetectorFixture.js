brjs.dashboard.app.testing.BrowserDetectorFixture = function()
{
	this.m_oBrowserDetector = null;
};
br.Core.inherit(brjs.dashboard.app.testing.BrowserDetectorFixture, br.test.Fixture);

brjs.dashboard.app.testing.BrowserDetectorFixture.prototype.setBrowserDetector = function(oBrowserDetector) 
{
	this.m_oBrowserDetector = oBrowserDetector;
};

brjs.dashboard.app.testing.BrowserDetectorFixture.prototype.canHandleExactMatch = function() 
{
	return false;
};

brjs.dashboard.app.testing.BrowserDetectorFixture.prototype.canHandleProperty = function(sProperty) 
{
	 return sProperty == "name" || sProperty == "version";
};

brjs.dashboard.app.testing.BrowserDetectorFixture.prototype._doGivenAndDoWhen = function(sPropertyName, vValue)
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
brjs.dashboard.app.testing.BrowserDetectorFixture.prototype.doGiven = brjs.dashboard.app.testing.BrowserDetectorFixture.prototype._doGivenAndDoWhen;
brjs.dashboard.app.testing.BrowserDetectorFixture.prototype.doWhen = brjs.dashboard.app.testing.BrowserDetectorFixture.prototype._doGivenAndDoWhen;

brjs.dashboard.app.testing.BrowserDetectorFixture.prototype.doThen = function(sPropertyName, vValue)
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

