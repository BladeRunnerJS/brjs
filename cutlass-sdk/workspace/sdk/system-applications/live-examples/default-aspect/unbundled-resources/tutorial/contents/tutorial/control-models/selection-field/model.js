novobank.example.DemoPresentationModel = function()
{
	var pHobbies = ["Cooking","Extreme Ironing","Bungeejumping", "Films", "Crochet"];
	this.hobbies = new caplin.presenter.node.SelectionField(pHobbies);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
