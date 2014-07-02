/**
 * @class
 * The <code>AlertFixture</code> allows for testing of browser alerts.
 * @interface
 */
br.test.AlertFixture = function()
{
	
};

br.Core.inherit(br.test.AlertFixture, br.test.Fixture);

br.test.AlertFixture.prototype.setUp = function()
{
	this.m_pAlertStack = [];
	this.m_fOriginalWindowAlertFunction = window.alert;
	
	var oThis = this;
	window.alert = function(sAlert) 
	{
		oThis.m_pAlertStack.push(sAlert);
	};
};

br.test.AlertFixture.prototype.tearDown = function()
{
	window.alert = this.m_fOriginalWindowAlertFunction;
	assertTrue("there were alerts triggered that were not expected in the test", this.m_pAlertStack.length === 0);
};

br.test.AlertFixture.prototype.doGiven = function(sPropertyName, vValue)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "given is not supported by AlertFixture");
};

br.test.AlertFixture.prototype.doWhen = function(sPropertyName, vValue)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "when is not supported by AlertFixture");
};

br.test.AlertFixture.prototype.doThen = function(sPropertyName, vValue)
{
	if (this.m_pAlertStack.length < 1) {
		fail("no alerts were triggered");
	}
	assertEquals("expected alert message '" + vValue + "', but was '" + this.m_pAlertStack[0] + "'", vValue, this.m_pAlertStack[0]);
	this.m_pAlertStack.shift();
};

br.test.AlertFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
};


br.test.AlertFixture.prototype.canHandleExactMatch = function()
{
	return true;
};

br.test.AlertFixture.prototype.canHandleProperty = function(sProperty)
{
	return false;
};
