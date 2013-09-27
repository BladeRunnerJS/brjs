TestFixtureTest = TestCase("TestFixtureTest");

TestFixtureTest.prototype.setUp = function()
{
	this.m_fOrigStartingContinuesFrom = window.startingContinuesFrom;
	this.m_fOrigFinishedContinuesFrom = window.finishedContinuesFrom;
	this.m_fOrigRuns = window.runs;
	runs = function(fMethod) {
		fMethod.apply(this, arguments);
	};
};

TestFixtureTest.prototype.tearDown = function()
{
	window.startingContinuesFrom = this.m_fOrigStartingContinuesFrom;
	window.finishedContinuesFrom = this.m_fOrigFinishedContinuesFrom;
	runs = this.m_fOrigRuns;
};

TestFixtureTest.prototype.test_doThenWithInvalidPropertyNameThrowsException = function()
{
	var oTestFixture = new br.test.TestFixture();
	
	assertException("1a", function() {
		oTestFixture.doThen("invalid-property-name", "property-value");
	}, br.Errors.INVALID_TEST);
};

TestFixtureTest.prototype.test_doWhenThrowsException = function()
{
	var oTestFixture = new br.test.TestFixture();
	
	assertException("1a", function() {
		oTestFixture.doWhen("property-name", "property-value");
	}, br.Errors.INVALID_TEST);
};

TestFixtureTest.prototype.test_doThenThrowsException = function()
{
	var oTestFixture = new br.test.TestFixture();
	
	assertException("1a", function() {
		oTestFixture.doWhen("property-name", "property-value");
	}, br.Errors.INVALID_TEST);
};

TestFixtureTest.prototype.test_continuesFromCausesTheRightFunctionsToBeInvoked = function()
{
	var oTestFixture = new br.test.TestFixture();
	var bStartInvoked = false;
	var bFinishInvoked = false;
	
	startingContinuesFrom = function() {
		bStartInvoked = true;
	};
	
	finishedContinuesFrom = function() {
		bFinishInvoked = true;
	};
	
	oTestFixture.doGiven("continuesFrom", "test-name");
	assertTrue("1a", bStartInvoked);
	assertTrue("1b", bFinishInvoked);
};
