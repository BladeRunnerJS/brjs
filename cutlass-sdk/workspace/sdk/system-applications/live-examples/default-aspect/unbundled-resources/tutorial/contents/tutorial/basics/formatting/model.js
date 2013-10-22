novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.property.Property(100.4199);
	this.amount.addFormatter(live.examples.presenter.tutorial.DecimalFormatter, {dp: 2});
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
