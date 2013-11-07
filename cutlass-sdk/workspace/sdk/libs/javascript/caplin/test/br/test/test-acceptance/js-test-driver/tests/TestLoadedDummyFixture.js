

TestLoadedDummyFixture = function()
{
	
};
br.inherit(TestLoadedDummyFixture, br.test.Fixture);


TestLoadedDummyFixture.prototype.canHandleProperty = function()
{
	return true;
};

TestLoadedDummyFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

TestLoadedDummyFixture.prototype.doGiven = function(sProperty, vValue)
{
	return vValue;
};

TestLoadedDummyFixture.prototype.doWhen = function(sProperty, vValue)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "TestLoadedDummyFixture can't be used in a When.");
};

TestLoadedDummyFixture.prototype.doThen = function(sProperty, vValue) {
	return true;
}
