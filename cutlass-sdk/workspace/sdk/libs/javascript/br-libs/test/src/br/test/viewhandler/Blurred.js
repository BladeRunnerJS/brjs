br.Core.thirdparty("jquery");

/**
 * @class
 * <code>Blurred ViewFixtureHandler</code> can be used to trigger <code>blur</code> or <code>focus</code> events on the view element.
 * Example usage:
 * <p>
 * <code>and("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).blurred => true");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Blurred = function()
{
};

br.Core.implement(br.test.viewhandler.Blurred, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.Blurred.prototype.set = function(eElement, vValue)
{
	if( !br.test.viewhandler.Focused.isFocusableElement(eElement) || eElement.disabled )
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'blurred' property is not available on non-focusable or disabled elements.");
	}
	
	if(vValue === true)
	{
		eElement.blur();
		jQuery(eElement).trigger("blur");
		
		if(eElement.tagName.toLowerCase() == "input")
		{
			jQuery(eElement).trigger("change");
		}
	}
	else if(vValue === false)
	{
		eElement.focus();
	}
	else
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'blurred' property only takes boolean values.");
	}
};

br.test.viewhandler.Blurred.prototype.get = function(eElement)
{
	if(!br.test.viewhandler.Focused.isFocusableElement(eElement))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'blurred' property is not available on non-focusable elements.");
	}

	if(eElement === document.activeElement)
	{
		return false;
	}
	return true;
};
