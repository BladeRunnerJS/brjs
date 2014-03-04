/**
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.OnKeyDown = function()
{
};

br.Core.implement(br.test.viewhandler.OnKeyDown, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.OnKeyDown.prototype.set = function(eElement, mValues)
{
	br.test.Utils.fireKeyEvent(eElement, "keydown", mValues, null);
};

br.test.viewhandler.OnKeyDown.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The keyDown event cannot be used in a doGiven or doThen");
};