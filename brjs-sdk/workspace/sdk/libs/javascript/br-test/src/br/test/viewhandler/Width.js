br.Core.thirdparty("jquery");

/**
 * @class
 * <code>Width ViewFixtureHandler</code> can be used to get width of a view element.
 * Example usage:
 * <p>
 * <code>then("dynamicComponent.view.(.component).width = 100");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Width = function()
{
};

br.Core.implement(br.test.viewhandler.Width, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.Width.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The width attribute for a element cannot be set directly and should be set via the viewModel.");
};

br.test.viewhandler.Width.prototype.get = function(eElement)
{
	return jQuery(eElement).width();
};
