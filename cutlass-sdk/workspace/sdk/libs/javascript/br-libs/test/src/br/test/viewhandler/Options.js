br.Core.thirdparty("jquery");

/**
 * @class
 * <code>Options ViewFixtureHandler</code> can be used to set or get the value of <code>options</code> property
 * for a SELECT view element.
 * Example usage:
 * <p>
 * <code>then("form.model.payment.options = ['credit','debit']");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Options = function()
{
};

br.test.viewhandler.Options.prototype.set = function(eElement, pValues)
{
	if (eElement.tagName.toLowerCase() !== "select")
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'options' property is only available for SELECT elements.");
	}
	if (!(pValues instanceof Array))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'options' property can only take an Array as its value.");
	}
	eElement.innerHTML = "";
	for (var idx = 0, max = pValues.length; idx < max; idx++)
	{
		var eNewOption = document.createElement("option");
		eNewOption.innerHTML = pValues[idx].toString();
		eElement.appendChild(eNewOption);
	}
};

br.test.viewhandler.Options.prototype.get = function(eElement)
{
	if (eElement.tagName.toLowerCase() !== "select")
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'options' property is only available for SELECT elements.");
	}
	var pOptions = [];
	jQuery(eElement).find("option").each(function(i,eOption){
		pOptions.push(eOption.innerHTML);
	});
	return pOptions;
};

br.Core.implement(br.test.viewhandler.Options, br.test.viewhandler.ViewFixtureHandler);