require('br-presenter/_resources-test-at/html/test-form.html');
var oo = require('br/Core');
var FixtureFactory = require('br-test/FixtureFactory');
var TestLoadedDummyFixture = require('br-test/_test-src/TestLoadedDummyFixture');

ViewFixtureTestsFixtureFactory = function()
{
};
oo.implement(ViewFixtureTestsFixtureFactory, FixtureFactory);

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

module.exports = ViewFixtureTestsFixtureFactory;
