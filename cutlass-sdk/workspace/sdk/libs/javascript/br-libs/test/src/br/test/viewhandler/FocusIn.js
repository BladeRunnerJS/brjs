br.Core.thirdparty("jquery");

/**
 * @class
 * <code>FocusIn ViewFixtureHandler</code> can be used to trigger <code>focusin</code> on a view element.
 * Example usage:
 * <p>
 * <code>and("form.view.(#theField).focusIn => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.FocusIn = function()
{
};

br.Core.implement(br.test.viewhandler.FocusIn, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.FocusIn.prototype.set = function(eElement)
{
	jQuery(eElement).trigger('focusin');
};

br.test.viewhandler.FocusIn.prototype.get = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The focusIn event cannot be used in a doGiven or doThen");
};
