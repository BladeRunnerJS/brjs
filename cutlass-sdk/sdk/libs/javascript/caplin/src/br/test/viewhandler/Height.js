br.thirdparty("jquery");

/**
 * @class
 * <code>Height ViewFixtureHandler</code> can be used to get height of a view element.
 * Example usage:
 * <p>
 * <code>then("dynamicComponent.view.(.component).height = 200");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Height = function()
{
};

br.implement(br.test.viewhandler.Height, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.Height.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The Height attribute for a element cannot be set directly and should be set via the viewModel.");
};

br.test.viewhandler.Height.prototype.get = function(eElement)
{
	return jQuery(eElement).height();
};
