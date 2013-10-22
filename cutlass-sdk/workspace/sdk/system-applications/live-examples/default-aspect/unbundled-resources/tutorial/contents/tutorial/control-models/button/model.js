novobank.example.DemoPresentationModel = function()
{
	this.myButton = new caplin.presenter.node.Button('');
	/* try changing these values */
	this.myButton.label.setValue("Click Me");
	this.myButton.enabled.setValue(true);
	this.myButton.visible.setValue(true);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype.buttonnClicked = function()
{
	alert("Hello World!");
};