novobank.example.DemoPresentationModel = function()
{
	this.text = new caplin.presenter.property.EditableProperty("TEXT");
	this.text.addParser(live.examples.presenter.tutorial.UpperCaseParser,{});
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
