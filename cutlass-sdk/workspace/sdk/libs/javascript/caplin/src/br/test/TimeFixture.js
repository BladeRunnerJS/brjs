/**
 * @class
 *
 * <code>TimeFixture</code> allows you to control when callbacks passed to window.setTimeout() and window.setInterval() are executed.<br>
 * Currently setInterval() is not fully supported. It only fires once instead of repeating.<br>
 * <br>
 * <b>timeMode property</b><br>
 * There are two modes supported by the <code>TimeFixture</code>: 
 * <ul>
 *  <li>'NextStep' (default) - will automatically fire all timers at the end of each 'given, when or then' step (including 'and' steps)</li>
 *  <li>'Manual' - will stop the automatic-firing of timers on each 'given, when or then' step</li>
 * </ul>
 * 
 * <b>passedBy property</b><br>  
 * When  in 'Manual' mode, 'passedBy' is defined in milliseconds. Any function(s) registered to wait for a period less than 
 * (or equal to) the  stated 'passedBy' value are then executed in ascending time order.<br>
 * <br>
 * <b>Example using 'NextStep' mode (default):</b><br>
 * 	<code>
 *  	given("time.timeMode = NextStep")<br>
 *  		and(...)<br>
 *      // All timed events fired for the 'and' step above<br>
 *  		and(...)<br>
 * 		// All timed events fired for the 'and' step above<br>
 *  	when(...)<br>
 *      // All timed events set up in the "when" step are fired<br> 
 *  	then(...)<br>
 * 	</code>
 * <br>
 * <b>Example using 'Manual' mode:</b><br>
 * <code>
 *  	given("time.timeMode = Manual")<br>
 *  		and(...)<br>
 *  	when(...)<br>
 *  		and("time.passedBy = 2000") <br>
 *  	// timed events from the 'given' and 'when' steps set to execute 2 seconds in the future are fired<br>
 *  	then(...)<br>
 * </code>
 * 
 * @implements br.test.Fixture
 */
br.test.TimeFixture = function(oTimeUtility)
{
	this.m_oTimeUtility = oTimeUtility;
};

br.inherit(br.test.TimeFixture, br.test.Fixture);

br.test.TimeFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

br.test.TimeFixture.prototype.tearDown = function()
{
	this.m_oTimeUtility.reset();
};

br.test.TimeFixture.prototype.canHandleProperty = function(sProperty)
{
	return sProperty === "timeMode" || sProperty === "passedBy";
};

br.test.TimeFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
};

/**
 * 
 * @param {String} sPropertyName The property name (e.g. 'time.timeMode' or 'time.passedBy')
 * @param {Variant} vValue The value to set 
 * 
 * @see br.test.Fixture#doGiven
 */
br.test.TimeFixture.prototype.doGiven = function(sPropertyName, vValue)
{
	this._configureTimeUtility(sPropertyName, vValue);
};

/**
 * @param {String} sPropertyName The property name (e.g. 'time.timeMode' or 'time.passedBy')
 * @param {Variant} vValue The value to set 
 * 
 * @see br.test.Fixture#doWhen
 */
br.test.TimeFixture.prototype.doWhen = function(sPropertyName, vValue)
{
	this._configureTimeUtility(sPropertyName, vValue);
};

/**
 * TimeFixture does not support doThen.
 * 
 * @param {String} sPropertyName The property name 
 * @param {Variant} vValue The value to set
 * 
 * @see br.test.Fixture#doThen
 */
br.test.TimeFixture.prototype.doThen = function(sPropertyName, vValue)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "then is not supported by TimeFixture");
};

br.test.TimeFixture.prototype._configureTimeUtility = function(sPropertyName, vValue)
{
	switch(sPropertyName)
	{
		case "timeMode":
			this.m_oTimeUtility.setTimeMode(vValue);
			break;
		case "passedBy":
			this.m_oTimeUtility.executeCapturedFunctions(vValue);
			break;
	}
};