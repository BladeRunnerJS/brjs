br.Core.thirdparty("jquery");

/**
 * @class
 * <code>BorderWidth ViewFixtureHandler</code> can be used to test the border width of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).borderwidth = '10'");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.BorderWidth = function()
{
};

br.test.viewhandler.BorderWidth.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "BorderWidth can't be used in a Given or When clause.");
};

br.test.viewhandler.BorderWidth.prototype.get = function(eElement)
{ 
	return parseInt(jQuery(eElement)[0].style.borderWidth);
};

br.Core.implement(br.test.viewhandler.BorderWidth, br.test.viewhandler.ViewFixtureHandler);