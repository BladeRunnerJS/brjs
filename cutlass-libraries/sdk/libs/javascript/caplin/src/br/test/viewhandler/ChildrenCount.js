br.thirdparty("jquery");

/**
 * @class
 * <code>ChildrenCount ViewFixtureHandler</code> can be used to get number of child elements for a view element.
 * Example usage:
 * <p>
 * <code>and("example.view.(select).childrenCount = 5");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.ChildrenCount = function()
{
};

br.implement(br.test.viewhandler.ChildrenCount, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.ChildrenCount.prototype.get = function(eElement)
{
	return jQuery(eElement).children().length;
};

br.test.viewhandler.ChildrenCount.prototype.set = function(eElement, vValue)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "ChildrenCount value can not be set on an object and therefore should only be used in a then clause.");
};
