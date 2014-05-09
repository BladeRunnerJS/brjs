br.Core.thirdparty("presenter-knockout");

/**
 * @private
 */
br.presenter.view.knockout.KnockoutProperty = function()
{
	// call super constructor
	br.presenter.view.knockout.KnockoutObservable.call(this);
};
br.Core.extend(br.presenter.view.knockout.KnockoutProperty, br.presenter.view.knockout.KnockoutObservable);

/**
 * @private
 */
br.presenter.view.knockout.KnockoutProperty.prototype.getValueForKnockout = function ()
{
	if (arguments.length > 0)
	{
		throw new br.Errors.InvalidParametersError("getValueForKnockout cannot write as this is not an EditableProperty");
	}
	
	presenter_ko.dependencyDetection.registerDependency(this); // The caller only needs to be notified of changes if they did a "read" operation
	return this.getFormattedValue();
};

br.presenter.view.knockout.KnockoutProperty.prototype.peek = function ()
{
	return this.getFormattedValue();
};

/**
 * @private
 * @static
 */
br.presenter.view.knockout.KnockoutProperty.createArrayMethod = function(sMethod)
{
	return function()
	{
		var pUnderlyingArray = this.getValue();
		var pNewArray = pUnderlyingArray.splice(0);
		Array.prototype[sMethod].apply(pNewArray, arguments);
		this.setValue(pNewArray);
	};
};

// TODO: we need some presenter CTs that interact with the view while the view is connected since otherwise this code is not being tested
br.presenter.view.knockout.KnockoutProperty.prototype.pop = br.presenter.view.knockout.KnockoutProperty.createArrayMethod("pop");
br.presenter.view.knockout.KnockoutProperty.prototype.push = br.presenter.view.knockout.KnockoutProperty.createArrayMethod("push");
br.presenter.view.knockout.KnockoutProperty.prototype.reverse = br.presenter.view.knockout.KnockoutProperty.createArrayMethod("reverse");
br.presenter.view.knockout.KnockoutProperty.prototype.shift = br.presenter.view.knockout.KnockoutProperty.createArrayMethod("shift");
br.presenter.view.knockout.KnockoutProperty.prototype.sort = br.presenter.view.knockout.KnockoutProperty.createArrayMethod("sort");
br.presenter.view.knockout.KnockoutProperty.prototype.splice = br.presenter.view.knockout.KnockoutProperty.createArrayMethod("splice");
br.presenter.view.knockout.KnockoutProperty.prototype.unshift = br.presenter.view.knockout.KnockoutProperty.createArrayMethod("unshift");
