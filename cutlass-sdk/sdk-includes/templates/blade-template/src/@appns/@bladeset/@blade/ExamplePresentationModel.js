@appns.@bladeset.@blade.ExamplePresentationModel = function()
{
	this.message = new br.presenter.property.Property("Hello World!" );	
};

br.extend(@appns.@bladeset.@blade.ExamplePresentationModel, br.presenter.PresentationModel);

@appns.@bladeset.@blade.ExamplePresentationModel.prototype.buttonClicked = function()
{
	alert(this.message.getValue());
}
