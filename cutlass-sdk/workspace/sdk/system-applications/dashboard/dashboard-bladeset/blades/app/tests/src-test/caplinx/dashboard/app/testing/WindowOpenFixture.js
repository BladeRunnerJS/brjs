caplinx.dashboard.app.testing.WindowOpenFixture = function()
{
	this.bIgnoreEvents = false;
};
br.inherit(caplinx.dashboard.app.testing.WindowOpenFixture, br.test.Fixture);

caplinx.dashboard.app.testing.WindowOpenFixture.prototype.setWindowOpenerService = function(oWindowOpenerService)
{
	this.m_oWindowOpenerService = oWindowOpenerService;
};

caplinx.dashboard.app.testing.WindowOpenFixture.prototype.tearDown = function()
{
	if (!this.bIgnoreEvents)
	{
		assertEquals("window.open function calls were made which were not expected in the test",
			0, this.m_oWindowOpenerService.getOpenerRequests().length);
	}
};

caplinx.dashboard.app.testing.WindowOpenFixture.prototype.doGiven = function(sPropertyName, vValue)
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

caplinx.dashboard.app.testing.WindowOpenFixture.prototype.doWhen = function(sPropertyName, vValue)
{
	fail("when is not supported by WindowOpenFixture");
};

caplinx.dashboard.app.testing.WindowOpenFixture.prototype.doThen = function(sPropertyName, vValue)
{
	assertTrue("no window.open function calls were triggered", this.m_oWindowOpenerService.getOpenerRequests().length >= 1);
	
	sRequest = this.m_oWindowOpenerService.getOpenerRequests().shift();
	assertEquals("window.open was invoked with the wrong url", sRequest, vValue);
};

caplinx.dashboard.app.testing.WindowOpenFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
};

caplinx.dashboard.app.testing.WindowOpenFixture.prototype.canHandleExactMatch = function()
{
	return true
};

caplinx.dashboard.app.testing.WindowOpenFixture.prototype.canHandleProperty = function(sProperty)
{
	return sProperty == "ignoreEvents"
};

caplinx.dashboard.app.testing.WindowOpenFixture.prototype._getOpenerRequests = function()
{
	return this.m_oWindowOpenerService.getOpenerRequests().length;
};
