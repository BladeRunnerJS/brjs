/**
 * @class
 * <code>Focused ViewFixtureHandler</code> can be used to trigger <code>focus</code> and <code>blur</code> on a view element.
 * Example usage:
 * <p>
 * <code>and("form.view.(#theButton).focused => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Focused = function()
{
};

br.Core.implement(br.test.viewhandler.Focused, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.Focused.focusableElements = {"A" : true, "BODY" : true, "BUTTON" : true, "FRAME" : true, "IFRAME" : true, "IMG" : true, "INPUT" : true, "ISINDEX" : true,
		"OBJECT" : true, "SELECT" : true, "TEXTAREA" : true};

br.test.viewhandler.Focused.isFocusableElement = function(eElement)
{
	return (eElement.tabIndex > 0) || ((eElement.tabIndex === 0) && this.focusableElements[eElement.tagName]);
};

br.test.viewhandler.Focused.prototype.set = function(eElement, vValue)
{
	if( !br.test.viewhandler.Focused.isFocusableElement(eElement) || eElement.disabled )
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'focused' property is not available on non-focusable or disabled elements.");
	}

	if(vValue === true)
	{
		eElement.focus();
	}
	else if(vValue === false)
	{
		eElement.blur();
	}
	else
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'focused' property only takes boolean values.");
	}
};

br.test.viewhandler.Focused.prototype.get = function(eElement)
{
	if(!br.test.viewhandler.Focused.isFocusableElement(eElement))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'focused' property is not available on non-focusable elements.");
	}

	if(eElement === document.activeElement)
	{
		return true;
	}
	return false;
};
