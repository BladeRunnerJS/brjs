/**
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.OnKeyUp = function()
{
};

br.test.viewhandler.OnKeyUp.prototype.set = function(eElement, mValues)
{
	br.test.Utils.fireKeyEvent(eElement, "keyup", mValues.sKey, mValues);
};

br.test.viewhandler.OnKeyUp.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The keyUp event cannot be used in a doGiven or doThen");
};

br.Core.implement(br.test.viewhandler.OnKeyUp, br.test.viewhandler.ViewFixtureHandler);
