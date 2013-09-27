caplinx.dashboard.app.model.form.Field = function(sPlaceholder, vValue)
{
	// call super constructor
	caplin.presenter.node.Field.call(this, vValue);
	
	// all of our form elements are permanently visible, so we don't need this property
	delete this.visible;
	
	this.placeholder = new caplin.presenter.property.WritableProperty(sPlaceholder);
	this.hasFocus = new caplin.presenter.property.EditableProperty(false);
};
caplin.extend(caplinx.dashboard.app.model.form.Field, caplin.presenter.node.Field);
