brjs.dashboard.app.testing.WindowOpenFixture = function()
{
	this.bIgnoreEvents = false;
};
br.Core.inherit(brjs.dashboard.app.testing.WindowOpenFixture, br.test.Fixture);

brjs.dashboard.app.testing.WindowOpenFixture.prototype.setWindowOpenerService = function(oWindowOpenerService)
{
	this.m_oWindowOpenerService = oWindowOpenerService;
};

brjs.dashboard.app.testing.WindowOpenFixture.prototype.tearDown = function()
{
	if (!this.bIgnoreEvents)
	{
		assertEquals("window.open function calls were made which were not expected in the test",
			0, this.m_oWindowOpenerService.getOpenerRequests().length);
	}
};

brjs.dashboard.app.testing.WindowOpenFixture.prototype.doGiven = function(sPropertyName, vValue)
{
	if (sPropertyName == "ignoreEvents")
	{
		this.bIgnoreEvents = vValue;
	}
	else
	{
		fail("given not supported for " + sPropertyName);
	}
};

brjs.dashboard.app.testing.WindowOpenFixture.prototype.doWhen = function(sPropertyName, vValue)
{
	fail("when is not supported by WindowOpenFixture");
};

brjs.dashboard.app.testing.WindowOpenFixture.prototype.doThen = function(sPropertyName, vValue)
{
	assertTrue("no window.open function calls were triggered", this.m_oWindowOpenerService.getOpenerRequests().length >= 1);

	sRequest = this.m_oWindowOpenerService.getOpenerRequests().shift();
	assertEquals("window.open was invoked with the wrong url", vValue, sRequest);
};

brjs.dashboard.app.testing.WindowOpenFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
};

brjs.dashboard.app.testing.WindowOpenFixture.prototype.canHandleExactMatch = function()
{
	return true
};

brjs.dashboard.app.testing.WindowOpenFixture.prototype.canHandleProperty = function(sProperty)
{
	return sProperty == "ignoreEvents"
};

brjs.dashboard.app.testing.WindowOpenFixture.prototype._getOpenerRequests = function()
{
	return this.m_oWindowOpenerService.getOpenerRequests().length;
};
