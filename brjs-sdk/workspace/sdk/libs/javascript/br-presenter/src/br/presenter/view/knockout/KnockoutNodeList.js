br.Core.thirdparty("presenter-knockout");

/**
 * @private
 *
 */
br.presenter.view.knockout.KnockoutNodeList = function()
{
	// call super constructor
	br.presenter.view.knockout.KnockoutObservable.call(this);
};

br.Core.extend(br.presenter.view.knockout.KnockoutNodeList, br.presenter.view.knockout.KnockoutObservable);

/**
 * @private
 */
br.presenter.view.knockout.KnockoutNodeList.prototype.getValueForKnockout = function ()
{
	if (arguments.length > 0)
	{
		throw new br.Errors.InvalidParametersError("getValueForKnockout cannot write as we do not support user editable NodeLists");
	}
	
	presenter_knockout.dependencyDetection.registerDependency(this); // The caller only needs to be notified of changes if they did a "read" operation
	return this.m_pItems;
};

br.presenter.view.knockout.KnockoutNodeList.prototype.peek = function ()
{
	return this.m_pItems;
};

/**
 * @private
 */
br.presenter.view.knockout.KnockoutNodeList.prototype.getTemplateName = function(oViewModel)
{
	return oViewModel.getTemplateName();
};
