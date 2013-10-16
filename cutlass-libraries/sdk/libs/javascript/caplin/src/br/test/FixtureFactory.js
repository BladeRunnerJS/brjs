/**
 * Constructs a <code>FixtureFactory</code> .
 * @constructor
 * @interface
 * @class
 * An implementing FixtureFactory can have an optional <code>setUp</code> method which will be called 
 * before each test is executed and can be used to reset the state of a test and its stubs.
 */
br.test.FixtureFactory = function()
{
};

/**
 * This method is called once by the test-runner after the FixtureFactory is constructed. The implementation 
 * should add to the test runner all the fixtures that are needed by the tests. 
 * 
 * @param {br.test.FixtureRegistry} oFixtureRegistry The registry to which the fixtures should be registered.
 */
br.test.FixtureFactory.prototype.addFixtures = function(oFixtureRegistry)
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "FixtureFactory.addFixtures() has not been implemented.");
};
