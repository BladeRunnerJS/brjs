/**
 * @private
 * Utility class containing static methods that can be useful for controlling time in tests.
 */
br.test.TimeUtility = {};

/** @private */
br.test.TimeUtility.TIMER_ID = 0;
/** @private */
br.test.TimeUtility.MANUAL_TIME_MODE = "Manual";
/** @private */
br.test.TimeUtility.NEXT_STEP_TIME_MODE = "NextStep";

/** @private */
br.test.TimeUtility.timeMode = br.test.TimeUtility.NEXT_STEP_TIME_MODE;
/** @private */
br.test.TimeUtility.pCapturedTimerFunctionArgs = [];
/** @private */
br.test.TimeUtility._bHasReplacedTimerFunctions = false;
/** @private */
br.test.TimeUtility.bCaptureTimeoutAndIntervals = true;

/** @private */
br.test.TimeUtility.fCapturedTimersSort = function(oFirstFunction, oSecondFunction) {
	return oFirstFunction[1] - oSecondFunction[1];
};

/**
 * Reset this TimeUtility to its original state. Useful for testing.
 * 
 * @private 
 */
br.test.TimeUtility.reset = function()
{
	this.timeMode = this.NEXT_STEP_TIME_MODE;
	this.bCaptureTimeoutAndIntervals = true;
	
	this.clearCapturedFunctions();
	this.releaseTimerFunctions();
};

/**
 * Overrides the default <code>setTimeout</code> and <code>setInterval</code> methods.
 * This allows the storing of all functions passed in to those methods.
 *
 * @static
 */
br.test.TimeUtility.captureTimerFunctions = function()
{
	if(this.bCaptureTimeoutAndIntervals && this._bHasReplacedTimerFunctions == false)
	{
		this.ORIGINAL_SETTIMEOUT_FUNCTION = window.setTimeout;
		this.ORIGINAL_SETINTERVAL_FUNCTION = window.setInterval;
		this.ORIGINAL_CLEARTIMEOUT_FUNCTION = window.clearTimeout;
		this.ORIGINAL_CLEARINTERVAL_FUNCTION = window.clearInterval;
		
		window.setTimeout = br.test.TimeUtility.fCaptureArguments;
		window.setInterval = br.test.TimeUtility.fCaptureArguments;
		window.clearTimeout = br.test.TimeUtility.fClearTimer;
		window.clearInterval = br.test.TimeUtility.fClearTimer;
		
		this._bHasReplacedTimerFunctions = true;
	}
};

/**
 * @static
 * @return {Array} A list of <code>argument</code> objects that were passed into <code>setTimeout</code>
 * and <code>setInterval</code>.
 */
br.test.TimeUtility.getCapturedFunctions = function()
{
	var pCapturedFunctions = this.pCapturedTimerFunctionArgs.slice();
	return pCapturedFunctions.sort(this.fCapturedTimersSort);
};

/**
 * @private
 */
br.test.TimeUtility.clearCapturedFunctions = function()
{
	this.pCapturedTimerFunctionArgs.length = 0;
};

/**
 * Execute all captured functions that are set to be triggered within the passed in millisecond time value.
 * 
 * If no value is passed, this will execute all captured functions.
 * 
 * @static
 */
br.test.TimeUtility.executeCapturedFunctions = function(nMsToExecuteTo)
{
	this.pCapturedTimerFunctionArgs.sort(this.fCapturedTimersSort);
	
	for(var i = 0; i < this.pCapturedTimerFunctionArgs.length; i++)
	{
		var pCapturedFunction = this.pCapturedTimerFunctionArgs[i];
		
		if(nMsToExecuteTo == null || pCapturedFunction[1] <= nMsToExecuteTo)
		{
			pCapturedFunction[0]();
			this.pCapturedTimerFunctionArgs.splice(i, 1);
			i--;
		}
		else
		{
			pCapturedFunction[1] -= nMsToExecuteTo; 
		}
	}
};

/**
 * Execute all captured functions if we are in NEXT_STEP_TIME_MODE.
 * 
 * All functions will be cleared even if an error is thrown, although not
 * all functions will be executed.
 * 
 * Returns false if we are not in NEXT_STEP_TIME_MODE
 * 
 * @static
 */
br.test.TimeUtility.nextStep = function() {
	if (this.timeMode === this.NEXT_STEP_TIME_MODE) {
		try {
			this.executeCapturedFunctions();
		} finally {
			this.clearCapturedFunctions();
		}
		return true;
	} else {
		return false;
	}
};

/**
 * Sets the timer mode which controls when captured timeouts and intervals run.
 * 
 * @static
 */
br.test.TimeUtility.setTimeMode = function(sTimeMode)
{
	if(sTimeMode === this.MANUAL_TIME_MODE || sTimeMode === this.NEXT_STEP_TIME_MODE)
	{
		this.timeMode = sTimeMode;
	}
	else
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Incorrect time mode ("+sTimeMode+") set on TimeUtility.");
	}
};

/** @private */
br.test.TimeUtility.releaseTimerFunctions = function()
{
	if (this._bHasReplacedTimerFunctions) {
		window.setTimeout = this.ORIGINAL_SETTIMEOUT_FUNCTION;
		window.setInterval = this.ORIGINAL_SETINTERVAL_FUNCTION;
		window.clearTimeout = this.ORIGINAL_CLEARTIMEOUT_FUNCTION;
		window.clearInterval = this.ORIGINAL_CLEARINTERVAL_FUNCTION;
		
		this._bHasReplacedTimerFunctions = false;
		
		delete this.ORIGINAL_SETTIMEOUT_FUNCTION;
		delete this.ORIGINAL_SETINTERVAL_FUNCTION;
		delete this.ORIGINAL_CLEARTIMEOUT_FUNCTION;
		delete this.ORIGINAL_CLEARINTERVAL_FUNCTION;
	}
};

/** @private */
br.test.TimeUtility.fCaptureArguments = function()
{
	arguments.nTimerId = br.test.TimeUtility.TIMER_ID++;
	
	br.test.TimeUtility.pCapturedTimerFunctionArgs.push(arguments);
	
	return arguments.nTimerId;
};

/** @private */
br.test.TimeUtility.fClearTimer = function(nTimerId)
{
	var pCapturedFunctions = br.test.TimeUtility.pCapturedTimerFunctionArgs;
	
	pCapturedFunctions = pCapturedFunctions.filter(function(oFunction, nIndex, pFunctions){
		return oFunction.nTimerId !== nTimerId;
	});
	
	br.test.TimeUtility.pCapturedTimerFunctionArgs = pCapturedFunctions;
};