brjs.dashboard.app.model.form.Button = function(vValue, oActionObject, sActionMethod)
{
	// call super constructor
	br.presenter.node.Button.call(this, vValue);
	
	// all of our form elements are permanently visible, so we don't need this property
	delete this.visible;
	
	this.tooltipLabel = new br.presenter.property.WritableProperty();
	this.tooltipVisible = new br.presenter.property.WritableProperty(false);
	
	this.m_oActionObject = oActionObject;
	this.m_sActionMethod = sActionMethod;
};
br.Core.extend(brjs.dashboard.app.model.form.Button, br.presenter.node.Field);

brjs.dashboard.app.model.form.Button.prototype.action = function()
{
	this.m_oActionObject[this.m_sActionMethod]();
};
