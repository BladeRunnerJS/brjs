novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.node.Field("100000");
	this.amount.value.addValidator(new caplin.core.validator.NumericValidator("novobank.example.invalid.number"));
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
