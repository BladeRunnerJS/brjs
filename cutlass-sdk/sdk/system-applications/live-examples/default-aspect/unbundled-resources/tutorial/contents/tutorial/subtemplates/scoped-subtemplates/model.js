novobank.example.DemoPresentationModel = function()
{
	this.account = new caplin.presenter.node.Field("acct1");
	this.currency = new caplin.presenter.node.Field("GBP");
	this.leg1 = new novobank.example.TradeLegPresentationNode(10000);
	this.leg2 = new novobank.example.TradeLegPresentationNode(20000);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.TradeLegPresentationNode = function(legAmount)
{
	this.amount = new caplin.presenter.node.Field(legAmount);
};
caplin.extend(novobank.example.TradeLegPresentationNode, caplin.presenter.node.PresentationNode);
