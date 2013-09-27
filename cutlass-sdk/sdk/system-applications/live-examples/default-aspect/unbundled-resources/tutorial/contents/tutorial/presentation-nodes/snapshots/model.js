novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.node.Field(10000);
	this.account = new caplin.presenter.node.Field("acct1");
	var oAllProperties = this.properties();
	this.m_oSnapshot = oAllProperties.snapshot();
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype.reset = function()
{
	this.m_oSnapshot.apply();
};
