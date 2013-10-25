/**
 * @class
 * <code>MouseOut ViewFixtureHandler</code> can be used to trigger <code>mouseout</code> event for a view element.
 * Example usage:
 * <p>
 * <code>when("test.page.(#aRealButton).mouseOut => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.MouseOut = function()
{
};

br.implement(br.test.viewhandler.MouseOut, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.MouseOut.prototype.set = function(eElement, mValues)
{
	br.test.Utils.fireMouseEvent(eElement, 'mouseout', mValues);
};

br.test.viewhandler.MouseOut.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The mouseOut event cannot be used in a doGiven or doThen");
};
