var br = require('br/Core');
var FixtureFactory = require('br/test/FixtureFactory');

ViewFixtureTestsFixtureFactory = function()
{
};

br.implement(ViewFixtureTestsFixtureFactory, FixtureFactory);

ViewFixtureTestsFixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	oFixtureRegistry.addFixture("page", new TestLoadedDummyFixture());
};

ViewFixtureTestsFixtureFactory.prototype.setUp = function()
{
	// This is to get around an issue in IE10 that prevents focus moving correctly when .focus() is called on
	// an element in some circumstances.
	document.body.focus();
};
