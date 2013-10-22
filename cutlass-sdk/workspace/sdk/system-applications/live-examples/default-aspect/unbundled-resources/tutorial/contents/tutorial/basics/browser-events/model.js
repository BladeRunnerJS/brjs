novobank.example.DemoPresentationModel = function()
{
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype.executeTrade = function()
{
	alert("The Execute button was clicked");
};
