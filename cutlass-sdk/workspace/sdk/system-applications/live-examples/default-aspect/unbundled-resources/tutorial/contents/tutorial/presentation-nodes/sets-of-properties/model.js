novobank.example.DemoPresentationModel = function()
{
	this.amount = new caplin.presenter.node.Field(10000);
	this.account = new caplin.presenter.node.Field("acct1");
	this.m_oPropertiesToDisable = this.properties("enabled");
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype.disableFields = function()
{
	this.m_oPropertiesToDisable.setValue(false);
};

novobank.example.DemoPresentationModel.prototype.enableFields = function()
{
	this.m_oPropertiesToDisable.setValue(true);
};
