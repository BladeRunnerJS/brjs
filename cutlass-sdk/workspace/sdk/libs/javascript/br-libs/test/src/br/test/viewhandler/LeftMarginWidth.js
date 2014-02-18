br.Core.thirdparty("jquery");

/**
 * @class
 * <code>LeftMarginWidth ViewFixtureHandler</code> can be used to test the left margin width of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).leftMarginWidth = '10'");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.LeftMarginWidth = function()
{
};

br.test.viewhandler.LeftMarginWidth.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "LeftMarginWidth can't be used in a Given or When clause.");
};

br.test.viewhandler.LeftMarginWidth.prototype.get = function(eElement)
{ 
	var sMargin = jQuery(eElement)[0].style.margin;
	
	var	pWidthValues = sMargin.match(/\d+/g);
	
	return pWidthValues.length == 4 ? pWidthValues[3] : pWidthValues.length == 2 ? pWidthValues[1] : pWidthValues[0] ;
};

br.Core.implement(br.test.viewhandler.LeftMarginWidth, br.test.viewhandler.ViewFixtureHandler);
