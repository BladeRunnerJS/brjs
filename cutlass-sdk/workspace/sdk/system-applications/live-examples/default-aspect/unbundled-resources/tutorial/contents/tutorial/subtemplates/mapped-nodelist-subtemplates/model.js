novobank.example.DemoPresentationModel = function()
{
	var mLegs = {
			nearLeg: new novobank.example.TradeLegPresentationNode(100000,  "GBPUSD" ),
			farLeg: new novobank.example.TradeLegPresentationNode(150000, "EURGBP" )
	};
	this.legs = new caplin.presenter.node.MappedNodeList(mLegs);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype.clearNearLegAmount = function(sAmount, sCurrencyPair)
{
	// here we're referring to the near-leg by name
	this.legs.nearLeg.amount.setValue(0);
};

novobank.example.TradeLegPresentationNode = function(sAmount, sCurrencyPair)
{
	this.amount = new caplin.presenter.property.WritableProperty(sAmount);
	this.currencyPair = new caplin.presenter.property.WritableProperty(sCurrencyPair);
};
caplin.extend(novobank.example.TradeLegPresentationNode, caplin.presenter.node.PresentationNode);
