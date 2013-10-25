br.thirdparty("jquery");

/**
 * @class
 * <code>IsVisible ViewFixtureHandler</code> can be used to check if a view element is visible.
 * Example usage:
 * <p>
 * <code>then("form.view.(.orderSummary).isVisible = true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.IsVisible = function()
{
};

br.implement(br.test.viewhandler.IsVisible, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.IsVisible.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Visibility can't be used in a Given or When clause.");
};

br.test.viewhandler.IsVisible.prototype.get = function(eElement)
{
	// Definition of invisible from jQuery API ...
	// Elements can be considered hidden for several reasons:
	//
	//	- They have a CSS display value of none.
	//	- They are form elements with type="hidden".
	//	- Their width and height are explicitly set to 0.
	//	- An ancestor element is hidden, so the element is not shown on the page.
	//
	// NOTE: Elements with visibility: hidden or opacity: 0 are considered to
	// be visible, since they still consume space in the layout.
	
	var sVisibility = jQuery(eElement).css("visibility");
	return jQuery(eElement).is(":visible") && sVisibility != 'hidden';
};
