novobank.example.DemoPresentationModel = function()
{
	this.message = "This is a simple Presenter component";
	var oPresenterComponent = new caplin.presenter.component.PresenterComponent("presenter-component-template", this);
	this.component = new caplin.presenter.node.ComponentNode(oPresenterComponent);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
