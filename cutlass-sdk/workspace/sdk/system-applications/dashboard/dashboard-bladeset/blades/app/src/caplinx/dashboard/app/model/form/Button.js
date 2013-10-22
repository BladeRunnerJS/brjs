caplinx.dashboard.app.model.form.Button = function(vValue, oActionObject, sActionMethod)
{
	// call super constructor
	caplin.presenter.node.Button.call(this, vValue);
	
	// all of our form elements are permanently visible, so we don't need this property
	delete this.visible;
	
	this.tooltipLabel = new caplin.presenter.property.WritableProperty();
	this.tooltipVisible = new caplin.presenter.property.WritableProperty(false);
	
	this.m_oActionObject = oActionObject;
	this.m_sActionMethod = sActionMethod;
};
caplin.extend(caplinx.dashboard.app.model.form.Button, caplin.presenter.node.Field);

caplinx.dashboard.app.model.form.Button.prototype.action = function()
{
	this.m_oActionObject[this.m_sActionMethod]();
};
