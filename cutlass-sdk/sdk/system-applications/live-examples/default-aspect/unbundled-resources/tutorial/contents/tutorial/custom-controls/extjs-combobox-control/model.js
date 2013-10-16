novobank.example.DemoPresentationModel = function()
{
	this.tradeType = new caplin.presenter.node.SelectionField(["SWAP", "FORWARD", "SPOT"]);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
