br.Core.thirdparty("jquery");

/**
 * @class
 * <code>BackgroundImage ViewFixtureHandler</code> can be used to test the background image value.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).backgroundImage = 'images/image.png'");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.BackgroundImage = function()
{
};

br.test.viewhandler.BackgroundImage.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "BackgroundImage can't be used in a Given or When clause.");
};

br.test.viewhandler.BackgroundImage.prototype.get = function(eElement)
{ 
	var sProperty = "div." + eElement.className;
	return jQuery(sProperty)[0].style.backgroundImage
};

br.Core.implement(br.test.viewhandler.BackgroundImage, br.test.viewhandler.ViewFixtureHandler);