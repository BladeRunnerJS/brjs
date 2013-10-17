@appns.@bladeset.@blade.ExampleClass = function()
{
	this.message = new caplin.presenter.property.Property("Hello World!" );	
};

caplin.extend(@appns.@bladeset.@blade.ExampleClass, caplin.presenter.PresentationModel);

@appns.@bladeset.@blade.ExampleClass.prototype.buttonClicked = function()
{
	alert(this.message.getValue());
}
