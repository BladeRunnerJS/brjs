novobank.example.DemoPresentationModel = function()
{
	var pLegs = [];
	pLegs.push(new novobank.example.TradeLegPresentationNode(100000, "GBPUSD", "near-leg-template"));
	pLegs.push(new novobank.example.TradeLegPresentationNode(150000, "EURGBP", "far-leg-template"));
	this.legs = new caplin.presenter.node.NodeList(pLegs);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.TradeLegPresentationNode = function(nAmount, sCurrencyPair, sTemplateName)
{
	this.amount = new caplin.presenter.property.WritableProperty(nAmount);
	this.currencyPair = new caplin.presenter.property.WritableProperty(sCurrencyPair);
	this.m_sTemplateName = sTemplateName;
};
caplin.extend(novobank.example.TradeLegPresentationNode, caplin.presenter.node.PresentationNode);
caplin.implement(novobank.example.TradeLegPresentationNode, caplin.presenter.node.TemplateAware);

novobank.example.TradeLegPresentationNode.prototype.getTemplateName = function()
{
	return this.m_sTemplateName;
};
