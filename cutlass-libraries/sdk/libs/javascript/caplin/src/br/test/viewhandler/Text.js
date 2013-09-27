br.thirdparty("jquery");

/**
 * @class
 * <code>Text ViewFixtureHandler</code> can be used to set or get <code>text</code> property of a view element.
 * Example usage:
 * <p>
 * <code>and("ticket.view.(.setupSpot .tradeDate label).text = 'Trade Date'");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Text = function()
{
};

br.implement(br.test.viewhandler.Text, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.Text.prototype.get = function(eElement)
{
	if (eElement.tagName.toLowerCase() === "input")
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Can not use the 'text' property on INPUT elements, try using 'value'.");
	}
	return jQuery(eElement).text();
};

br.test.viewhandler.Text.prototype.set = function(eElement, vValue)
{
	if (eElement.tagName.toLowerCase() === "input")
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Can not use the 'text' property on INPUT elements, try using 'value'.");
	}
	jQuery(eElement).text(vValue);
};
