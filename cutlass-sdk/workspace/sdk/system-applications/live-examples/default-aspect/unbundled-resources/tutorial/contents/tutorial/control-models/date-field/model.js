novobank.example.DemoPresentationModel = function()
{
	this.dateField = new caplin.presenter.node.DateField("2011-01-01");
	this.dateField.startDate.setValue("2001-01-01");
	this.dateField.endDate.setValue("2021-01-01");
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
