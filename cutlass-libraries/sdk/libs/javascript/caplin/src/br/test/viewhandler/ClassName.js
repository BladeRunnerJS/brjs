/**
 * @class
 * <code>ClassName ViewFixtureHandler</code> can be used to get a class of a view element.
 * Example usage:
 * <p>
 * <code>then("ticket.view.('#ticketContentAreaContainer').className = 'OpenSent'");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.ClassName = function()
{
};

br.implement(br.test.viewhandler.ClassName, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.ClassName.prototype.get = function(eElement)
{
	return eElement.className;
};

br.test.viewhandler.ClassName.prototype.set = function(eElement, vValue)
{
	if (typeof vValue !== "string") {
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "className can only be set to a String.");
	} else {
		eElement.className = vValue;
	}
};
