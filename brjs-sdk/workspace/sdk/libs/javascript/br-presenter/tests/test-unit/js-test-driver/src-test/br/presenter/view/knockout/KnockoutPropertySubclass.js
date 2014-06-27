br.presenter.view.knockout.KnockoutPropertySubclass = function()
{
	// call super constructor
	br.presenter.property.WritableProperty.call(this);
	
	this.m_nUpdateCounter = 0;
	this.m_vViewValue = null;
	
	this.addListener(this);
};
br.Core.extend(br.presenter.view.knockout.KnockoutPropertySubclass, br.presenter.property.WritableProperty);
br.Core.inherit(br.presenter.view.knockout.KnockoutPropertySubclass, br.presenter.property.PropertyListener);

br.presenter.view.knockout.KnockoutPropertySubclass.prototype.onPropertyUpdated = function()
{
	this.m_nUpdateCounter++;
};

br.presenter.view.knockout.KnockoutPropertySubclass.prototype.notifySubscribers = function(vValue, sEvent)
{
	this.m_vViewValue = vValue;
};
