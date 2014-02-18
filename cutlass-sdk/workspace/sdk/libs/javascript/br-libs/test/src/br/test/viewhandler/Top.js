br.Core.thirdparty("jquery");

/**
 * @class
 * <code>Top ViewFixtureHandler</code> can be used to get style.top value of a view element.
 * Example usage:
 * <p>
 * <code>then("dynamicComponent.view.(.component).top = 20");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Top = function()
{
};

br.test.viewhandler.Top.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The Top attribute for a element cannot be set directly and should be set via the viewModel.");
};

br.test.viewhandler.Top.prototype.get = function(eElement)
{
	return jQuery(eElement)[0].style.top;
};

br.Core.implement(br.test.viewhandler.Top, br.test.viewhandler.ViewFixtureHandler);
