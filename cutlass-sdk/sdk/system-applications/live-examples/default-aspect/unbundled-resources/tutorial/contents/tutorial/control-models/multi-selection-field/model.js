novobank.example.DemoPresentationModel = function()
{
	this.m_pHobbies = ["Cooking","Extreme Ironing","Bungeejumping", "Films", "Crochet"];
	var pChosen = ["Extreme Ironing", "Films"];
	this.hobbies = new caplin.presenter.node.MultiSelectionField(this.m_pHobbies, pChosen);
	
	this.noExtremeSports = new caplin.presenter.node.Field(false);
	this.noExtremeSports.label.setValue("No Extreme Sports");
	this.noExtremeSports.value.addChangeListener(this, "updateHobbies");
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);

novobank.example.DemoPresentationModel.prototype.updateHobbies = function()
{
	if(this.noExtremeSports.value.getValue())
	{
		this.hobbies.options.setOptions(["Cooking", "Films", "Technology"]);
	}
	else
	{
		this.hobbies.options.setOptions(this.m_pHobbies);
	}
};
