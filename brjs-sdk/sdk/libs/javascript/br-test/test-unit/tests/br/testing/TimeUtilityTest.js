TimeUtilityTest = TestCase("TimeUtilityTest");

TimeUtilityTest.prototype.setUp = function(){
};

TimeUtilityTest.prototype.tearDown = function()
{
	br.test.TimeUtility.reset();
};

TimeUtilityTest.prototype["test that captureTimerFunctions sets the timer functions to fCaptureArguments."] = function()
{
	br.test.TimeUtility.captureTimerFunctions();
	
	assertEquals(window.setTimeout, br.test.TimeUtility.fCaptureArguments);
	assertEquals(window.setInterval, br.test.TimeUtility.fCaptureArguments);
	assertNotEquals(window.setTimeout, br.test.TimeUtility.ORIGINAL_SETTIMEOUT_FUNCTION);
};

TimeUtilityTest.prototype["test that: Given I've done a captureTimerFunctions, releaseTimerFunctions puts the timer functions back the way they were before."] = function()
{
	var originalSetTimeout = window.setTimeout;
	var originalSetInterval = window.setInterval;

	br.test.TimeUtility.captureTimerFunctions();
	br.test.TimeUtility.releaseTimerFunctions();
	
	assertEquals(originalSetTimeout, window.setTimeout);
	assertEquals(originalSetInterval, window.setInterval);
};

TimeUtilityTest.prototype["test that: Given bCaptureTimerFunctions is set to false, the timer functions are left in their original state after a capture / release"] = function()
{
	var originalSetTimeout = window.setTimeout;
	var originalSetInterval = window.setInterval;

	br.test.TimeUtility.bCaptureTimeoutAndIntervals = false;
	
	br.test.TimeUtility.captureTimerFunctions();
	br.test.TimeUtility.releaseTimerFunctions();
	
	assertEquals(originalSetTimeout, window.setTimeout);
	assertEquals(originalSetInterval, window.setInterval);
};


TimeUtilityTest.prototype["test that captured functions can be retrieved."] = function()
{
	br.test.TimeUtility.captureTimerFunctions();
	
	window.setTimeout(function(){}, 10);
	window.setInterval(function(){}, 20);
	
	var pCapturedFunctions = br.test.TimeUtility.getCapturedFunctions();
	
	assertEquals(10, pCapturedFunctions[0][1]);
	assertEquals(20, pCapturedFunctions[1][1]);
};

TimeUtilityTest.prototype["test that captured functions can be executed."] = function()
{
	br.test.TimeUtility.captureTimerFunctions();
	
	var bTimeoutExecuted = false;
	var bIntervalExecuted = false;
	
	window.setTimeout(function(){bTimeoutExecuted = true}, 10);
	window.setInterval(function(){bIntervalExecuted = true}, 20);
	
	br.test.TimeUtility.executeCapturedFunctions();
	
	assertTrue(bTimeoutExecuted);
	assertTrue(bIntervalExecuted);
};

TimeUtilityTest.prototype["test that only functions scheduled to execute before a certain time are executed when providing a time argument to executeCapturedFunctions."] = function()
{
	br.test.TimeUtility.captureTimerFunctions();
	
	var bFirstTimeoutExecuted = false;
	var bSecondTimeoutExecuted = false;
	var bThirdTimeoutExecuted = false;

	window.setTimeout(function(){bThirdTimeoutExecuted = true;}, 30);
	window.setTimeout(function(){
		assertTrue(bFirstTimeoutExecuted);
		assertFalse(bThirdTimeoutExecuted);
		bSecondTimeoutExecuted = true;
	}, 20);
	window.setTimeout(function(){
		assertFalse(bSecondTimeoutExecuted);
		assertFalse(bThirdTimeoutExecuted);
		bFirstTimeoutExecuted = true;
	}, 10);
	
	br.test.TimeUtility.executeCapturedFunctions(20);
	
	assertTrue(bFirstTimeoutExecuted);
	assertTrue(bSecondTimeoutExecuted);
	assertFalse(bThirdTimeoutExecuted);
};

TimeUtilityTest.prototype["test that setting an invalid time mode throws an INVALID_TEST error."] = function()
{
	assertException("1a", function() {
		br.test.TimeUtility.setTimeMode("Nonsense");
	}, br.Errors.INVALID_TEST);
};

TimeUtilityTest.prototype["test that clearing an interval will stop it from being executed when executeCapturedFunctions is called."] = function()
{
	br.test.TimeUtility.captureTimerFunctions();
	
	var bTimeoutExecuted = false;
	var bIntervalExecuted = false;
	
	window.setTimeout(function(){bTimeoutExecuted = true}, 10);
	var nTimerId = window.setInterval(function(){bIntervalExecuted = true}, 20);
	
	clearInterval(nTimerId);
	
	br.test.TimeUtility.executeCapturedFunctions();
	
	assertTrue(bTimeoutExecuted);
	assertFalse(bIntervalExecuted);
};

TimeUtilityTest.prototype["test that setting bCaptureTimerFunctions to false stops functions from being captured."] = function()
{
	br.test.TimeUtility.bCaptureTimeoutAndIntervals = false; //Don't capture timers.
	
	br.test.TimeUtility.captureTimerFunctions();
	
	var bTimeoutExecuted = false;
	var bIntervalExecuted = false;
	
	window.setTimeout(function(){bTimeoutExecuted = true}, 10);
	window.setInterval(function(){bIntervalExecuted = true}, 20);
	
	br.test.TimeUtility.executeCapturedFunctions();
	
	assertFalse(bTimeoutExecuted);
	assertFalse(bIntervalExecuted);
};

TimeUtilityTest.prototype["test that intervals and timeouts are executed in the correct order by executeCapturedFunctions"] = function()
{
	br.test.TimeUtility.captureTimerFunctions();

	var bTimeoutExecuted = false;
	var bIntervalExecuted = false;
	
	window.setInterval(function(){bIntervalExecuted = true}, 20);
	window.setTimeout(function(){bTimeoutExecuted = true; assertFalse(bIntervalExecuted)}, 10);
	
	br.test.TimeUtility.executeCapturedFunctions();
	
	assertTrue(bTimeoutExecuted);
	assertTrue(bIntervalExecuted);
};

TimeUtilityTest.prototype["test that subsequent calls to executeCapturedFunctions move time forward"] = function()
{
	br.test.TimeUtility.captureTimerFunctions();
	var bFirstTimeoutExecuted = false;
	var bSecondTimeoutExecuted = false;
	var bThirdTimeoutExecuted = false;

	window.setTimeout(function(){ bThirdTimeoutExecuted = true; }, 30);
	window.setTimeout(function(){ bSecondTimeoutExecuted = true; }, 20);
	window.setTimeout(function(){ bFirstTimeoutExecuted = true;	}, 10);
	
	br.test.TimeUtility.executeCapturedFunctions(10);
	
	assertTrue(bFirstTimeoutExecuted);
	assertFalse(bSecondTimeoutExecuted);
	assertFalse(bThirdTimeoutExecuted);
	
	br.test.TimeUtility.executeCapturedFunctions(10);

	assertTrue(bFirstTimeoutExecuted);
	assertTrue(bSecondTimeoutExecuted);
	assertFalse(bThirdTimeoutExecuted);

	br.test.TimeUtility.executeCapturedFunctions(10);
	
	assertTrue(bFirstTimeoutExecuted);
	assertTrue(bSecondTimeoutExecuted);
	assertTrue(bThirdTimeoutExecuted);
};