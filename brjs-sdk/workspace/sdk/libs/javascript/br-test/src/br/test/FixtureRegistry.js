/**
 * @class
 * The <code>FixtureRegistry</code> allows for registration of fixtures for a 
 * specified scope. 
 * @interface
 */
br.test.FixtureRegistry = function()
{
};

/**
 * Adds a fixture to the registry. 
 * 
 * @param {String} sScope The scope to which the fixture should be registered.
 * @param {br.test.Fixture} oFixture The fixture to register.
 */
br.test.FixtureRegistry.prototype.addFixture = function(sScope, oFixture)
{
};
