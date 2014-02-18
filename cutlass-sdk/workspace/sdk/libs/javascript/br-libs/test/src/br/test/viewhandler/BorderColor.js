br.Core.thirdparty("jquery");

/**
 * @class
 * <code>BorderColor ViewFixtureHandler</code> can be used to test the border color of an element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).bordercolor = '#1111FF'");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.BorderColor = function()
{
};

br.test.viewhandler.BorderColor.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "BorderWidth can't be used in a Given or When clause.");
};

br.test.viewhandler.BorderColor.prototype.get = function(eElement)
{ 
	var sColor = (jQuery(eElement)[0].style.borderColor).toLowerCase(); 
	
	var digits = /rgba?\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)/.exec(sColor);
	var sHexColor;
	
	if (digits)
	{
		var red = parseInt(digits[1]);
		var green = parseInt(digits[2]);
		var blue = parseInt(digits[3]);
		
		var rgb = 1 << 24 | blue | (green << 8) | (red << 16);
		
		sHexColor = '#' + rgb.toString(16).substr(1);
	}
	else if (sColor.match(/^#[0-9a-f]{6}/i))
	{
		sHexColor = sColor;
	}
	else
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Color format was not expected");
	}
	return sHexColor.toUpperCase();
};

br.Core.implement(br.test.viewhandler.BorderColor, br.test.viewhandler.ViewFixtureHandler);