/**
 * @class
 * <code>RightClicked ViewFixtureHandler</code> can be used to trigger <code>contextmenu</code> event for a view element.
 * Example usage:
 * <p>
 * <code>when("test.page.(#aRealButton).rightclicked => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.RightClicked = function()
{
};
br.implement(br.test.viewhandler.RightClicked, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.RightClicked.prototype.set = function(eElement)
{
	br.test.Utils.fireMouseEvent(eElement, "contextmenu");
};

br.test.viewhandler.RightClicked.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Clicked can't be used in a then clause.");
};
