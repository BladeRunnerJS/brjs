br.Core.thirdparty("jquery");

/**
 * @class
 * <code>Selected ViewFixtureHandler</code> can be used to get or set <code>selected</code>
 * property of an OPTION view element.
 * Example usage:
 * <p>
 * <code>when("demo.view.(#multiSelectBox option:last).selected => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Selected = function(){
};

br.Core.implement(br.test.viewhandler.Selected, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.Selected.prototype.get = function(eElement)
{
	if (eElement.selected === undefined)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Only Option elements have the 'selected' property.");
	}
	return eElement.selected;
};

br.test.viewhandler.Selected.prototype.set = function(eElement, vValue)
{
	if (eElement.selected === undefined)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Only Option elements have their 'selected' property set.");
	}
	if (!(vValue === true || vValue === false))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "the 'selected' property can only be set to true or false.");
	}
	if (eElement.selected != vValue) {
		eElement.selected = vValue;
		jQuery(eElement).parent('select').trigger("change");
	}
};
