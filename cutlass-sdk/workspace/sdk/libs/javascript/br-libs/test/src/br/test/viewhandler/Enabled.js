br.Core.thirdparty("jquery");

/**
 * @class
 * <code>Enabled ViewFixtureHandler</code> can be used to enable and disable a view element
 * by setting the <code>disabled<code> attribute.
 * Example usage:
 * <p>
 * <code>and("form.view.(.close).enabled = true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Enabled = function(){
};

br.test.viewhandler.Enabled.prototype.get = function(eElement)
{

    var pElementsToTest = jQuery(eElement).add(jQuery(eElement).parents());

    for(var i = 0; i < pElementsToTest.length; i++)

    {
        var eElementToTest = jQuery(pElementsToTest[i]);
        if(eElementToTest.is(":disabled"))
        {
            return false;
        }
    }
    return true;
};

br.test.viewhandler.Enabled.prototype.set = function(eElement, vValue)
{
	// Using strict equality to detect non-boolean vValue's
	if (vValue === true)
	{
		// Disabled elements make their descendants disabled too, so if someone
		// tries to enable such a child, FAIL.
		if (jQuery(eElement).parents(":disabled").length > 0)
		{
			throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Can not enable element with a disabled ancestor.")
		}
		eElement.disabled = false;
	}
	else if (vValue === false)
	{
		eElement.disabled = true;
	}
	else
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "enabled can only be set with a boolean value.");
	}
};

br.Core.implement(br.test.viewhandler.Enabled, br.test.viewhandler.ViewFixtureHandler);