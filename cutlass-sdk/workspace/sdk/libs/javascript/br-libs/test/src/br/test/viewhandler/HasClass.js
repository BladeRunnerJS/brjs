/**
 * @class
 * <code>HasClass ViewFixtureHandler</code> can be used to verify that a view element
 * has a particular class. Example usage:
 * <p>
 * <code>then("form.view.(.orderAmount .amountValue input).hasClass = 'has-error'");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.HasClass = function()
{
};

br.Core.implement(br.test.viewhandler.HasClass, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.HasClass.prototype.get = function(eElement, sClassName)
{
	if(eElement.className.match("(^| )" + sClassName + "($| )")) {
		return sClassName;
	}
	else {
		return null;
	}
};

br.test.viewhandler.HasClass.prototype.set = function(eElement, sClassName)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "hasClass can't be used in a Given or When clause.");
};
