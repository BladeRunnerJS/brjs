'use strict';

/**
 * @module br/test/TimeUtility
 */

var Errors = require('br/Errors');

/**
 * @private
 * @class
 * @alias module:br/test/TimeUtility
 *
 * @classdesc
 * Utility class containing static methods that can be useful for controlling time in tests.
 */
var TimeUtility = {};

/** @private */
TimeUtility.TIMER_ID = 0;
/** @private */
TimeUtility.MANUAL_TIME_MODE = 'Manual';
/** @private */
TimeUtility.NEXT_STEP_TIME_MODE = 'NextStep';

/** @private */
TimeUtility.timeMode = TimeUtility.NEXT_STEP_TIME_MODE;
/** @private */
TimeUtility.pCapturedTimerFunctionArgs = [];
/** @private */
TimeUtility._bHasReplacedTimerFunctions = false;
/** @private */
TimeUtility.bCaptureTimeoutAndIntervals = true;

/** @private */
TimeUtility.fCapturedTimersSort = function(firstFunction, secondFunction) {
	return firstFunction[1] - secondFunction[1];
};

/**
 * Reset this TimeUtility to its original state. Useful for testing.
 * @private
 */
TimeUtility.reset = function() {
	this.timeMode = this.NEXT_STEP_TIME_MODE;
	this.bCaptureTimeoutAndIntervals = true;

	this.clearCapturedFunctions();
	this.releaseTimerFunctions();
};

/**
 * Overrides the default <code>setTimeout</code> and <code>setInterval</code> methods. This allows the storing of all
 *  functions passed in to those methods.
 */
TimeUtility.captureTimerFunctions = function() {
	if (this.bCaptureTimeoutAndIntervals && this._bHasReplacedTimerFunctions === false) {
		this.ORIGINAL_SETTIMEOUT_FUNCTION = window.setTimeout;
		this.ORIGINAL_SETINTERVAL_FUNCTION = window.setInterval;
		this.ORIGINAL_CLEARTIMEOUT_FUNCTION = window.clearTimeout;
		this.ORIGINAL_CLEARINTERVAL_FUNCTION = window.clearInterval;

		window.setTimeout = TimeUtility.fCaptureArguments;
		window.setInterval = TimeUtility.fCaptureArguments;
		window.clearTimeout = TimeUtility.fClearTimer;
		window.clearInterval = TimeUtility.fClearTimer;

		this._bHasReplacedTimerFunctions = true;
	}
};

/**
 * @return {Array} A list of <code>argument</code> objects that were passed into <code>setTimeout</code> and
 *  <code>setInterval</code>.
 */
TimeUtility.getCapturedFunctions = function() {
	var capturedFunctions = this.pCapturedTimerFunctionArgs.slice();

	return capturedFunctions.sort(this.fCapturedTimersSort);
};

/** @private */
TimeUtility.clearCapturedFunctions = function() {
	this.pCapturedTimerFunctionArgs.length = 0;
};

/**
 * Execute all captured functions that are set to be triggered within the passed in millisecond time value. If no value
 *  is passed, this will execute all captured functions.
 */
TimeUtility.executeCapturedFunctions = function(nMsToExecuteTo) {
	var capturedFunction, innerCapturedFunction;

	this.pCapturedTimerFunctionArgs.sort(this.fCapturedTimersSort);

	for (var idx = 0; idx < this.pCapturedTimerFunctionArgs.length; idx++) {
		capturedFunction = this.pCapturedTimerFunctionArgs[idx];

		if (nMsToExecuteTo == null || capturedFunction[1] <= nMsToExecuteTo) {
			var timerArgsLength = this.pCapturedTimerFunctionArgs.length;
			capturedFunction[0]();
			
			for (var idy = timerArgsLength; idy < this.pCapturedTimerFunctionArgs.length; idy++) {
				innerCapturedFunction = this.pCapturedTimerFunctionArgs[idy];
				innerCapturedFunction[1] += capturedFunction[1];
			}
			
			this.pCapturedTimerFunctionArgs.splice(idx, 1);
			idx--;
		} else {
			capturedFunction[1] -= nMsToExecuteTo;
		}
	}
};

/**
 * Execute all captured functions if we are in NEXT_STEP_TIME_MODE. All functions will be cleared even if an error is
 *  thrown, although not all functions will be executed. Returns false if we are not in NEXT_STEP_TIME_MODE

 */
TimeUtility.nextStep = function() {
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
 */
TimeUtility.setTimeMode = function(timeMode) {
	if (timeMode === this.MANUAL_TIME_MODE || timeMode === this.NEXT_STEP_TIME_MODE) {
		this.timeMode = timeMode;
	} else {
		throw new Errors.InvalidTestError('Incorrect time mode (' + timeMode + ') set on TimeUtility.');
	}
};

/** @private */
TimeUtility.releaseTimerFunctions = function() {
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
TimeUtility.fCaptureArguments = function() {
	arguments.nTimerId = TimeUtility.TIMER_ID++;

	TimeUtility.pCapturedTimerFunctionArgs.push(arguments);

	return arguments.nTimerId;
};

/** @private */
TimeUtility.fClearTimer = function(timerId) {
	var capturedFunctions = TimeUtility.pCapturedTimerFunctionArgs;

	capturedFunctions = capturedFunctions.filter(function(capturedFunction){
		return capturedFunction.nTimerId !== timerId;
	});

	TimeUtility.pCapturedTimerFunctionArgs = capturedFunctions;
};

module.exports = TimeUtility;
