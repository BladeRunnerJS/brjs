/**
 * @private
 *
 * @implements br.test.Fixture
 */
br.test.TestFixture = function(oGwtTestRunner)
{
	this.m_oGwtTestRunner = oGwtTestRunner;
};

br.inherit(br.test.TestFixture, br.test.Fixture);

br.test.TestFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

br.test.TestFixture.prototype.canHandleProperty = function(sProperty)
{
	return sProperty == "continuesFrom";
};

br.test.TestFixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("page", new br.test.ViewFixture("body"));
};

br.test.TestFixture.prototype.doGiven = function(sPropertyName, vValue)
{
	startingContinuesFrom(vValue);
	finishedContinuesFrom();
};

br.test.TestFixture.prototype.doWhen = function(sPropertyName, vValue)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "when is not supported by TestFixture");
};

br.test.TestFixture.prototype.doThen = function(sPropertyName, vValue)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "then is not supported by TestFixture");
};