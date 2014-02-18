/**
 * @class
 * <code>ReadOnly ViewFixtureHandler</code> can be used to set or get the <code>readonly</code> attribute of an input view element
 * Example usage:
 * <p>
 * <code>then("form.view.(.totalValue input).readonly = true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Readonly = function()
{
};

br.test.viewhandler.Readonly.prototype.set = function(eElement, vValue)
{
	eElement.readOnly= (vValue === true);
};
br.test.viewhandler.Readonly.prototype.get = function(eElement)
{
	return eElement.readOnly;
};

br.Core.implement(br.test.viewhandler.Readonly, br.test.viewhandler.ViewFixtureHandler);