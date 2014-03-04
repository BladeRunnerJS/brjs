/**
 * @class
 * <code>Checked ViewFixtureHandler</code> can be used to trigger <code>checked</code> property of a checkbox or a radiobutton.
 * Example usage:
 * <p>
 * <code>and("example.view.(input:eq(0)).checked = false");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Checked = function()
{
};

br.Core.implement(br.test.viewhandler.Checked, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.Checked.prototype.get = function(eElement)
{
	if (eElement.checked === undefined)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Only checkboxes and radio buttons have the 'checked' property.");
	}
	return eElement.checked;
};

br.test.viewhandler.Checked.prototype.set = function(eElement, vValue)
{
	if (eElement.checked === undefined)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Only checkboxes and radio buttons can have the 'checked' property set.");
	}
	if (!(vValue === true || vValue === false))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "the 'checked' property can only be set to true or false.");
	}
	
	br.test.Utils.fireDomEvent(eElement, 'click');
	br.test.Utils.fireDomEvent(eElement, 'change');
	eElement.checked = vValue;
};
