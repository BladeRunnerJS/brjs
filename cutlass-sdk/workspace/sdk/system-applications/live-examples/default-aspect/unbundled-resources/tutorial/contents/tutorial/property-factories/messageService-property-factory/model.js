novobank.example.DemoPresentationModel = function()
{
	var oRecordPropertyFactory = new caplin.presenter.domain.property.MessageServicePropertyFactory("/FX/EURUSD");
	this.bestBid = oRecordPropertyFactory.getProperty("BestBid");
	this.bestAsk = oRecordPropertyFactory.getProperty("BestAsk");
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
