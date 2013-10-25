OrderOfClickAndBlurPresentationModel = function()
{
	this.textInput = new br.presenter.property.EditableProperty("");
	this.storedText = new br.presenter.property.WritableProperty("");
};
br.extend(OrderOfClickAndBlurPresentationModel, br.presenter.PresentationModel);

OrderOfClickAndBlurPresentationModel.prototype.storeCurrentText = function()
{
	this.storedText.setValue(this.textInput.getValue());
};
