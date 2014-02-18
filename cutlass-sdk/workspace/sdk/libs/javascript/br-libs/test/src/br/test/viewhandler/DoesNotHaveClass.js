/**
 * @class
 * <code>DoesNotHaveClass ViewFixtureHandler</code> can be used to verify that a view element
 * does not have a particular class. Example usage:
 * <p>
 * <code>then("test.page.(#aRealButton).doesNotHaveClass = 'hover'");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.DoesNotHaveClass = function()
{
};

br.test.viewhandler.DoesNotHaveClass.prototype.get = function(eElement, sClassName)
{
	if(eElement.className.match("(^| )" + sClassName + "($| )")) {
		return null;
	}
	else {
		return sClassName;
	}
};

br.test.viewhandler.DoesNotHaveClass.prototype.set = function(eElement, sClassName)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "doesNotHaveClass can't be used in a Given or When clause.");
};

br.Core.implement(br.test.viewhandler.DoesNotHaveClass, br.test.viewhandler.ViewFixtureHandler);