/**
 * @interface
 * @class
 * <code>Fixture</code> is the interface for individual fixtures added to the GWTTestRunner. The purpose of
 * a Fixture is to enable tests to manipulate and access a specific area of the system under tests using the 
 * GWT (given-when-then) BDD format.
 */
br.test.Fixture = function()
{
};

/**
 * This method is called just before a GWT test. This optional interface method can be implemented if the fixture 
 * is required to correctly set up the system-under-test before each test.
 */
br.test.Fixture.prototype.setUp = function()
{
	// optional interface method
};

/**
 * This method is called just after a GWT test. This optional interface method can be implemented if the fixture 
 * is required to correctly tear down the system-under-test after each test or to reset any state held in the fixture's
 * implementation.
 */
br.test.Fixture.prototype.tearDown = function()
{
	// optional interface method
};

/**
 * This optional interface method can be implemented by a Fixture for a complex system which can be conceptually 
 * decomposed into separate sub-systems, enabling the fixture to delegate the handling of some fixture properties 
 * to the sub-fixtures. This method is called by the GWTTestRunner.
 * 
 * @param {br.test.FixtureRegistry} oFixtureRegistry The registry to which the fixtures should be registered.
 */
br.test.Fixture.prototype.addSubFixtures = function(oFixtureRegistry)
{
	// optional interface method
};

br.test.Fixture.prototype.canHandleExactMatch = function()
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "Fixture.canHandleExactMatch() has not been implemented.");
};

/**
 * This method is called by the GWTTestRunner to check whether a property used in a GWT test is supported by 
 * the fixture.
 * 
 * @param {String} sProperty the property name to check.
 * @returns {Boolean} true if the fixture handles the property; false otherwise
 */
br.test.Fixture.prototype.canHandleProperty = function(sProperty)
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "Fixture.canHandleProperty() has not been implemented.");
};

/**
 * This method is called in order to manipulate a property on the system under test in a given clause.
 * 
 * @param {String} sPropertyName The property to be changed.
 * @param {String} vValue The new value of the property.
 */
br.test.Fixture.prototype.doGiven = function(sPropertyName, vValue)
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "Fixture.doGiven() has not been implemented.");
};

/**
 * This method is called in order to manipulate a property on the system under test in a when clause.
 * 
 * @param {String} sPropertyName The property to be changed.
 * @param {String} vValue The new value of the property.
 */
br.test.Fixture.prototype.doWhen = function(sPropertyName, vValue)
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "Fixture.doWhen() has not been implemented.");
};

/**
 * This method is called in order to assert a property's value on the system under test.
 * 
 * @param {String} sPropertyName The property name to assert.
 * @param {String} vValue The value to assert.
 */
br.test.Fixture.prototype.doThen = function(sPropertyName, vValue)
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "Fixture.doThen() has not been implemented.");
};
