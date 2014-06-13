brjs.dashboard.app.model.form.Field = function(sPlaceholder, vValue)
{
	// call super constructor
	br.presenter.node.Field.call(this, vValue);
	
	// all of our form elements are permanently visible, so we don't need this property
	delete this.visible;
	
	this.placeholder = new br.presenter.property.WritableProperty(sPlaceholder);
	this.hasFocus = new br.presenter.property.EditableProperty(false);
};
br.Core.extend(brjs.dashboard.app.model.form.Field, br.presenter.node.Field);
