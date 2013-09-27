novobank.example.DemoPresentationModel = function()
{
	this.buysell = new caplin.presenter.node.SelectionField({"gbp": "GBP", "usd": "USD"});
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
