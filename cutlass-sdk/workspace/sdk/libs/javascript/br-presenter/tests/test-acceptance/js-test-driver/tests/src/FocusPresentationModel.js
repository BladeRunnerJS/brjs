FocusPresentationModel = function()
{
	this.inputA = new br.presenter.node.Field("Input A");
	this.inputB = new br.presenter.node.Field("Input B");
	
	this.inputA.hasFocus = new br.presenter.property.EditableProperty(false);
	this.inputB.hasFocus = new br.presenter.property.EditableProperty(false);
	
	this.inputA.hasFocus.setValue(false);
	this.inputB.hasFocus.setValue(false);
};
br.Core.extend(FocusPresentationModel, br.presenter.PresentationModel);
