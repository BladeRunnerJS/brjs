/**
 * @class
 * <code>MouseUp ViewFixtureHandler</code> can be used to trigger <code>mouseup</code> event for a view element.
 * Example usage:
 * <p>
 * <code>when("test.page.(#aRealButton).mouseUp => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.MouseUp = function()
{
};

br.implement(br.test.viewhandler.MouseUp, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.MouseUp.prototype.set = function(eElement, mValues)
{
	br.test.Utils.fireMouseEvent(eElement, 'mouseup', mValues);
};

br.test.viewhandler.MouseUp.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The mouseUp event cannot be used in a doGiven or doThen");
};
