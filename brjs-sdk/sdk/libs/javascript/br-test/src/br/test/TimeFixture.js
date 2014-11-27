'use strict';

/**
 * @module br/test/TimeFixture
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br/test/Fixture');

/**
 * @class
 * @alias module:br/test/TimeFixture
 * @implements module:br/test/Fixture
 * 
 * @classdesc
 * <code>TimeFixture</code> allows you to control when callbacks passed to window.setTimeout() and window.setInterval() 
 *  are executed.<br/>
 *  Currently setInterval() is not fully supported. It only fires once instead of repeating.<br/><br/>
 *  <b>timeMode property</b><br/>
 *  There are two modes supported by the <code>TimeFixture</code>:
 *  <ul>
 *   <li>'NextStep' (default) - will automatically fire all timers at the end of each 'given, when or then' step 
 *    (including 'and' steps)</li>
 *   <li>'Manual' - will stop the automatic-firing of timers on each 'given, when or then' step</li>
 *  </ul>
 *
 *  <b>passedBy property</b><br/>
 *  When  in 'Manual' mode, 'passedBy' is defined in milliseconds. Any function(s) registered to wait for a period less 
 *   than (or equal to) the  stated 'passedBy' value are then executed in ascending time order.<br/><br/>
 *  <b>Example using 'NextStep' mode (default):</b><br/>
 *   <code>
 *      given("time.timeMode = 'NextStep'")<br/>
 *          and(...)<br/>
 *      // All timed events fired for the 'and' step above<br/>
 *          and(...)<br/>
 *      // All timed events fired for the 'and' step above<br/>
 *      when(...)<br/>
 *      // All timed events set up in the "when" step are fired<br/>
 *      then(...)<br/>
 *   </code>
 *  <br/>
 *  <b>Example using 'Manual' mode:</b><br/>
 *  <code>
 *      given("time.timeMode = 'Manual'")<br/>
 *          and(...)<br/>
 *      when(...)<br/>
 *          and("time.passedBy => 2000") <br/>
 *      // timed events from the 'given' and 'when' steps set to execute 2 seconds in the future are fired<br/>
 *      then(...)<br/>
 *  </code>
 */
function TimeFixture(timeUtility) {
	this.m_oTimeUtility = timeUtility;
};
br.inherit(TimeFixture, Fixture);

TimeFixture.prototype.canHandleExactMatch = function() {
	return false;
};

TimeFixture.prototype.tearDown = function() {
	this.m_oTimeUtility.reset();
};

TimeFixture.prototype.canHandleProperty = function(propertyName) {
	return propertyName === 'timeMode' || propertyName === 'passedBy';
};

TimeFixture.prototype.addSubFixtures = function(fixtureRegistry) {
};

/**
 * @see br.test.Fixture#doGiven
 * @param {String} propertyName The property name (e.g. 'time.timeMode' or 'time.passedBy').
 * @param {Variant} value The value to set.
 */
TimeFixture.prototype.doGiven = function(propertyName, value) {
	this._configureTimeUtility(propertyName, value);
};

/**
 * @see br.test.Fixture#doWhen
 * @param {String} propertyName The property name (e.g. 'time.timeMode' or 'time.passedBy').
 * @param {Variant} value The value to set.
 */
TimeFixture.prototype.doWhen = function(propertyName, value) {
	this._configureTimeUtility(propertyName, value);
};

/**
 * TimeFixture does not support doThen.
 *
 * @param {String} propertyName The property name.
 * @param {Variant} value The value to set.
 *
 * @see br.test.Fixture#doThen
 */
TimeFixture.prototype.doThen = function(propertyName, value) {
	throw new Errors.InvalidTestError('then is not supported by TimeFixture');
};

/** @private */
TimeFixture.prototype._configureTimeUtility = function(propertyName, value) {
	switch (propertyName) {
		case 'timeMode':
			this.m_oTimeUtility.setTimeMode(value);
			break;
		case 'passedBy':
			this.m_oTimeUtility.executeCapturedFunctions(value);
			break;
	}
};

module.exports = TimeFixture;
