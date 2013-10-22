br.thirdparty("jquery");

/**
 * @class
 * <code>FocusOut ViewFixtureHandler</code> can be used to trigger <code>focusout</code> on a view element.
 * Example usage:
 * <p>
 * <code>and("form.view.(#theField).focusOut => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.FocusOut = function()
{
};

br.implement(br.test.viewhandler.FocusOut, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.FocusOut.prototype.set = function(eElement)
{
	jQuery(eElement).trigger('focusout');
};

br.test.viewhandler.FocusOut.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The focusOut event cannot be used in a doGiven or doThen");
};
