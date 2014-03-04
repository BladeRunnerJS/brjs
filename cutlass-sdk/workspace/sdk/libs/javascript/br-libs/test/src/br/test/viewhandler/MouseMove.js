/**
 * @class
 * <code>MouseMove ViewFixtureHandler</code> can be used to trigger <code>mousemove</code> event for a view element.
 * Example usage:
 * <p>
 * <code>when("test.page.(#aRealButton).mouseMove => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.MouseMove = function()
{
};

br.Core.implement(br.test.viewhandler.MouseMove, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.MouseMove.prototype.set = function(eElement, mValues)
{
	br.test.Utils.fireMouseEvent(eElement, 'mousemove', mValues);
};

br.test.viewhandler.MouseMove.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The mouseMove event cannot be used in a doGiven or doThen");
};
