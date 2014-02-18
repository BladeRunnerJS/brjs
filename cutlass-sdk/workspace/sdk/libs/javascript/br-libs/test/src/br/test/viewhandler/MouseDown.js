/**
 * @class
 * <code>MouseDown ViewFixtureHandler</code> can be used to trigger <code>mousedown</code> event for a view element.
 * Example usage:
 * <p>
 * <code>when("test.page.(#aRealButton).mouseDown => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.MouseDown = function()
{
};

br.test.viewhandler.MouseDown.prototype.set = function(eElement, mValues)
{
	br.test.Utils.fireMouseEvent(eElement, 'mousedown', mValues);
};

br.test.viewhandler.MouseDown.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The mouseDown event cannot be used in a doGiven or doThen");
};

br.Core.implement(br.test.viewhandler.MouseDown, br.test.viewhandler.ViewFixtureHandler);
