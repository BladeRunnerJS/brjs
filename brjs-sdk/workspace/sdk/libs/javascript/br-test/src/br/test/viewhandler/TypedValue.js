br.Core.thirdparty("jquery");

/**
 * @class
 * <code>TypedValue ViewFixtureHandler</code> can be used to simulate typing a value into an input view element.
 * Example usage:
 * <p>
 * <code>when("form.view.([identifier=\'orderForm\'] .order_amount .order_amount_input input).typedValue => 'abc'");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.TypedValue = function()
{
};

br.Core.implement(br.test.viewhandler.TypedValue, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.TypedValue.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'typedValue' property can't be used in a Then clause, try using 'value'.");
};

br.test.viewhandler.TypedValue.prototype.set = function(eElement, sValue)
{
	   if (eElement.value === undefined)
	   {
			  throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The element you tried to use 'typedValue' on doesn't have a value field to simulate typing on.");
	   }
	   
	   //Check whether the last active element wants us to fire a change event. 
	   if(document.activeElement && document.activeElement.bFireChangeEventWhenNextElementIsActivated)
	   {
			  delete document.activeElement.bFireChangeEventWhenNextElementIsActivated;
			  br.test.Utils.fireDomEvent(document.activeElement, 'change');
	   }
	  
	   eElement.focus();
	   jQuery(eElement).trigger('focusin');
	  
	   for (var i = 0, max = sValue.length; i < max; ++i)
	   {
			  var sKey = sValue.charAt(i);
			 
			  br.test.Utils.fireKeyEvent(eElement, "keydown", sKey);
			  eElement.value += sKey;
			  br.test.Utils.fireKeyEvent(eElement, "keypress", sKey);
			  br.test.Utils.fireKeyEvent(eElement, "keyup", sKey);
	   }
	  
	   //Request the next active element to fire a change event 
	   eElement.bFireChangeEventWhenNextElementIsActivated = true;
};

