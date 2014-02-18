br.Core.thirdparty("jquery");

/**
 * @class
 * <code>Value ViewFixtureHandler</code> can be used to set or get <code>value</code> property of a view element.
 * Example usage:
 * <p>
 * <code>then("form.view.(.orderSummary .orderAmount .native input).value = '50'");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Value = function()
{
};

br.test.viewhandler.Value.prototype.get = function(eElement)
{
	if (eElement.value === undefined)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The element you tried to use the 'value' property on doesn't have one.");
	}
	elementValue = jQuery(eElement).val();
	return elementValue;
};

br.test.viewhandler.Value.prototype.set = function(eElement, vValue)
{
	if (eElement.value === undefined)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The element you tried to use the 'value' property on doesn't have one.");
	}
	
	try { delete eElement.fireOnChange; } catch (e) { }
	jQuery(eElement).val(vValue).change();
};

br.Core.implement(br.test.viewhandler.Value, br.test.viewhandler.ViewFixtureHandler);
