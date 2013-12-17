/**
 * @class
 * <code>MouseOver ViewFixtureHandler</code> can be used to trigger <code>mouseover</code> event for a view element.
 * Example usage:
 * <p>
 * <code>when("test.page.(#aRealButton).mouseOver => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.MouseOver = function()
{
};

br.Core.implement(br.test.viewhandler.MouseOver, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.MouseOver.prototype.set = function(eElement, mValues)
{
	br.test.Utils.fireMouseEvent(eElement, 'mouseover', mValues);
};

br.test.viewhandler.MouseOver.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The mouseOver event cannot be used in a doGiven or doThen");
};
