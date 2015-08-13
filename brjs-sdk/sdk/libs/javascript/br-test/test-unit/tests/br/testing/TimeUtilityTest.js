(function() {
    var Errors = require("br/Errors");
    var TimeUtility = require("br/test/TimeUtility");
    TimeUtilityTest = TestCase("TimeUtilityTest");

    TimeUtilityTest.prototype.setUp = function(){
    };

    TimeUtilityTest.prototype.tearDown = function()
    {
        TimeUtility.reset();
    };

    TimeUtilityTest.prototype["test that captureTimerFunctions sets the timer functions to fCaptureArguments."] = function()
    {
        TimeUtility.captureTimerFunctions();
        
        assertEquals(window.setTimeout, TimeUtility.fCaptureArguments);
        assertEquals(window.setInterval, TimeUtility.fCaptureArguments);
        assertNotEquals(window.setTimeout, TimeUtility.ORIGINAL_SETTIMEOUT_FUNCTION);
    };

    TimeUtilityTest.prototype["test that: Given I've done a captureTimerFunctions, releaseTimerFunctions puts the timer functions back the way they were before."] = function()
    {
        var originalSetTimeout = window.setTimeout;
        var originalSetInterval = window.setInterval;

        TimeUtility.captureTimerFunctions();
        TimeUtility.releaseTimerFunctions();
        
        assertEquals(originalSetTimeout, window.setTimeout);
        assertEquals(originalSetInterval, window.setInterval);
    };

    TimeUtilityTest.prototype["test that: Given bCaptureTimerFunctions is set to false, the timer functions are left in their original state after a capture / release"] = function()
    {
        var originalSetTimeout = window.setTimeout;
        var originalSetInterval = window.setInterval;

        TimeUtility.bCaptureTimeoutAndIntervals = false;
        
        TimeUtility.captureTimerFunctions();
        TimeUtility.releaseTimerFunctions();
        
        assertEquals(originalSetTimeout, window.setTimeout);
        assertEquals(originalSetInterval, window.setInterval);
    };


    TimeUtilityTest.prototype["test that captured functions can be retrieved."] = function()
    {
        TimeUtility.captureTimerFunctions();
        
        window.setTimeout(function(){}, 10);
        window.setInterval(function(){}, 20);
        
        var pCapturedFunctions = TimeUtility.getCapturedFunctions();
        
        assertEquals(10, pCapturedFunctions[0][1]);
        assertEquals(20, pCapturedFunctions[1][1]);
    };

    TimeUtilityTest.prototype["test that captured functions can be executed."] = function()
    {
        TimeUtility.captureTimerFunctions();
        
        var bTimeoutExecuted = false;
        var bIntervalExecuted = false;
        
        window.setTimeout(function(){bTimeoutExecuted = true}, 10);
        window.setInterval(function(){bIntervalExecuted = true}, 20);
        
        TimeUtility.executeCapturedFunctions();
        
        assertTrue(bTimeoutExecuted);
        assertTrue(bIntervalExecuted);
    };

    TimeUtilityTest.prototype["test that only functions scheduled to execute before a certain time are executed when providing a time argument to executeCapturedFunctions."] = function()
    {
        TimeUtility.captureTimerFunctions();
        
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
        
        TimeUtility.executeCapturedFunctions(20);
        
        assertTrue(bFirstTimeoutExecuted);
        assertTrue(bSecondTimeoutExecuted);
        assertFalse(bThirdTimeoutExecuted);
    };

    TimeUtilityTest.prototype["test that setting an invalid time mode throws an INVALID_TEST error."] = function()
    {
        assertException("1a", function() {
            TimeUtility.setTimeMode("Nonsense");
        }, Errors.INVALID_TEST);
    };

    TimeUtilityTest.prototype["test that clearing an interval will stop it from being executed when executeCapturedFunctions is called."] = function()
    {
        TimeUtility.captureTimerFunctions();
        
        var bTimeoutExecuted = false;
        var bIntervalExecuted = false;
        
        window.setTimeout(function(){bTimeoutExecuted = true}, 10);
        var nTimerId = window.setInterval(function(){bIntervalExecuted = true}, 20);
        
        clearInterval(nTimerId);
        
        TimeUtility.executeCapturedFunctions();
        
        assertTrue(bTimeoutExecuted);
        assertFalse(bIntervalExecuted);
    };

    TimeUtilityTest.prototype["test that setting bCaptureTimerFunctions to false stops functions from being captured."] = function()
    {
        TimeUtility.bCaptureTimeoutAndIntervals = false; //Don't capture timers.
        
        TimeUtility.captureTimerFunctions();
        
        var bTimeoutExecuted = false;
        var bIntervalExecuted = false;
        
        window.setTimeout(function(){bTimeoutExecuted = true}, 10);
        window.setInterval(function(){bIntervalExecuted = true}, 20);
        
        TimeUtility.executeCapturedFunctions();
        
        assertFalse(bTimeoutExecuted);
        assertFalse(bIntervalExecuted);
    };

    TimeUtilityTest.prototype["test that intervals and timeouts are executed in the correct order by executeCapturedFunctions"] = function()
    {
        TimeUtility.captureTimerFunctions();

        var bTimeoutExecuted = false;
        var bIntervalExecuted = false;
        
        window.setInterval(function(){bIntervalExecuted = true}, 20);
        window.setTimeout(function(){bTimeoutExecuted = true; assertFalse(bIntervalExecuted)}, 10);
        
        TimeUtility.executeCapturedFunctions();
        
        assertTrue(bTimeoutExecuted);
        assertTrue(bIntervalExecuted);
    };

    TimeUtilityTest.prototype["test that subsequent calls to executeCapturedFunctions move time forward"] = function()
    {
        TimeUtility.captureTimerFunctions();
        var bFirstTimeoutExecuted = false;
        var bSecondTimeoutExecuted = false;
        var bThirdTimeoutExecuted = false;

        window.setTimeout(function(){ bThirdTimeoutExecuted = true; }, 30);
        window.setTimeout(function(){ bSecondTimeoutExecuted = true; }, 20);
        window.setTimeout(function(){ bFirstTimeoutExecuted = true;	}, 10);
        
        TimeUtility.executeCapturedFunctions(10);
        
        assertTrue(bFirstTimeoutExecuted);
        assertFalse(bSecondTimeoutExecuted);
        assertFalse(bThirdTimeoutExecuted);
        
        TimeUtility.executeCapturedFunctions(10);

        assertTrue(bFirstTimeoutExecuted);
        assertTrue(bSecondTimeoutExecuted);
        assertFalse(bThirdTimeoutExecuted);

        TimeUtility.executeCapturedFunctions(10);
        
        assertTrue(bFirstTimeoutExecuted);
        assertTrue(bSecondTimeoutExecuted);
        assertTrue(bThirdTimeoutExecuted);
    };
    
    TimeUtilityTest.prototype["test that timeouts can contain timeouts"] = function() {
    	TimeUtility.captureTimerFunctions();
    	var bOuterTimeoutExecuted = false;
    	var bNestedTimeoutExecuted = false;
    	var bSecondOuterTimeoutExecuted = false;
    
    	window.setTimeout(function(){
    		bOuterTimeoutExecuted = true;
    		window.setTimeout(function(){ bNestedTimeoutExecuted = true; }, 10);
    	}, 10);
    	window.setTimeout(function(){ bSecondOuterTimeoutExecuted = true; }, 20);
    	
    	TimeUtility.executeCapturedFunctions(10);
    	
    	assertTrue(bOuterTimeoutExecuted);
    	assertFalse(bNestedTimeoutExecuted);
    	assertFalse(bSecondOuterTimeoutExecuted);
    	
    	TimeUtility.executeCapturedFunctions(10);
    
    	assertTrue(bOuterTimeoutExecuted);
    	assertTrue(bNestedTimeoutExecuted);
    	assertTrue(bSecondOuterTimeoutExecuted);
    };
    
    TimeUtilityTest.prototype["test that timeouts can contain an arbitrary amount of nested timeouts"] = function() {
    	TimeUtility.captureTimerFunctions();
    	var bTimeout1Executed = false;
    	var bTimeout2Executed = false;
    	var bTimeout3Executed = false;
    	var bTimeout4Executed = false;
    
    	window.setTimeout(function(){
    		bTimeout1Executed = true;
    		window.setTimeout(function(){
    			bTimeout2Executed = true;
    			window.setTimeout(function(){
    				bTimeout3Executed = true;
    				window.setTimeout(function(){
    					bTimeout4Executed = true;
    				}, 10);
    			}, 10);
    		}, 10);
    	}, 10);
    	
    	TimeUtility.executeCapturedFunctions();
    	
    	assertTrue(bTimeout1Executed);
    	assertTrue(bTimeout2Executed);
    	assertTrue(bTimeout3Executed);
    	assertTrue(bTimeout4Executed);
    };

})();
