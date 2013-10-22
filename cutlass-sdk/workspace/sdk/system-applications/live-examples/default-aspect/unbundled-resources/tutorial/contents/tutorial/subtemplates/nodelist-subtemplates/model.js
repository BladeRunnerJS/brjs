novobank.example.DemoPresentationModel = function()
{
	var pLegs = [];
	pLegs.push(new novobank.example.TradeLegPresentationNode(100000, "GBPUSD"));
	pLegs.push(new novobank.example.TradeLegPresentationNode(150000, "EURGBP"));
	this.legs = new caplin.presenter.node.NodeList(pLegs);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.TradeLegPresentationNode = function(nAmount, sCurrencyPair)
{
	this.amount = new caplin.presenter.property.WritableProperty(nAmount);
	this.currencyPair = new caplin.presenter.property.WritableProperty(sCurrencyPair);
};
caplin.extend(novobank.example.TradeLegPresentationNode, caplin.presenter.node.PresentationNode);
