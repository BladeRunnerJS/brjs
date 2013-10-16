novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.node.Field(10000);
	this.amount.label.setValue(ct.i18n("novobank.example.amount.label"));
	this.amount.value.addValidator(
		new caplin.core.validator.NumericValidator("novobank.example.invalid.number"));
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
