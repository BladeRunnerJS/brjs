require('br-presenter/_resources-test-at/html/test-form.html');
var oo = require('br/Core');
var Errors = require('br/Errors');
var Fixture = require('br-test/Fixture');

function TestLoadedDummyFixture()
{
}
oo.inherit(TestLoadedDummyFixture, Fixture);

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
	throw new Errors.InvalidTestError("TestLoadedDummyFixture can't be used in a When.");
};

TestLoadedDummyFixture.prototype.doThen = function(sProperty, vValue) {
	return true;
};

module.exports = TestLoadedDummyFixture;
