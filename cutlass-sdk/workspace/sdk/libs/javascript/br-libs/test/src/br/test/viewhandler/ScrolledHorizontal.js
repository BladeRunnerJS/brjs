/**
 * @class
 * <code>ScrolledHorizontal ViewFixtureHandler</code> can be used to trigger a horizontal scroll on a view element.
 * </code>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.ScrolledHorizontal = function()
{
};

br.test.viewhandler.ScrolledHorizontal.prototype.set = function(eElement, nOffset)
{
	eElement.scrollLeft += parseFloat(nOffset);
	br.test.Utils.fireScrollEvent(eElement);
};

br.test.viewhandler.ScrolledHorizontal.prototype.get = function(eElement)
{
	return eElement.scrollLeft;
};

br.Core.implement(br.test.viewhandler.ScrolledHorizontal, br.test.viewhandler.ViewFixtureHandler);
