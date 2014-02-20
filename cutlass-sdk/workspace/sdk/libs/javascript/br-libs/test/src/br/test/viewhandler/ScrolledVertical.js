/**
 * @class
 * <code>ScrolledVertical ViewFixtureHandler</code> can be used to trigger a vertical scroll on a view element.
 * </code>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.ScrolledVertical = function()
{
};

br.Core.implement(br.test.viewhandler.ScrolledVertical, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.ScrolledVertical.prototype.set = function(eElement, nOffset)
{
	eElement.scrollTop += parseFloat(nOffset);
	br.test.Utils.fireScrollEvent(eElement);
};

br.test.viewhandler.ScrolledVertical.prototype.get = function(eElement)
{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "ScrolledVertical can't be used in a then clause.");
};
