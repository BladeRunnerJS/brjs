(function() {
    var Errors = require("br/Errors");
    var TimeFixture = require("br/test/TimeFixture");
    var Core = require("br/Core");
    Core.thirdparty('mock4js');

    TimeFixtureTest = TestCase("TimeFixtureTest");

    TimeFixtureTest.prototype.setUp = function()
    {
        this.m_oStubTimeUtility = {};
        this.m_oTimeFixture = new TimeFixture(this.m_oStubTimeUtility);
    };

    TimeFixtureTest.prototype.tearDown = function()
    {
    };

    TimeFixtureTest.prototype.test_timeModeIsSet = function()
    {
        this.m_oStubTimeUtility.setTimeMode = function(sTimerMode) {
            assertEquals(sTimerMode, "Manual");
        };
        
        this.m_oTimeFixture.doGiven("timeMode", "Manual");
    };

    TimeFixtureTest.prototype.test_executeCapturedFunctionsIfSetToTriggerWithinMilliseconds = function()
    {
        this.m_oStubTimeUtility.executeCapturedFunctionsIfSetToTriggerWithinMilliseconds = function(nMsToExecuteTo) {
            assertEquals(nMsToExecuteTo, 15);
        };
        
        this.m_oTimeFixture.doGiven("elapseTime", 15);
    };

    TimeFixtureTest.prototype.test_doThenThrowsException = function()
    {
        var oTimeFixture = new TimeFixture();
        
        assertException("1a", function() {
            oTimeFixture.doThen("property-name", "property-value");
        }, Errors.INVALID_TEST);
    };
})();
