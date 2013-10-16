novobank.example.DemoPresentationModel = function()
{
	var enteredDate = new Date();
	var startDate = "2012-01-01";
	var endDate = "2022-01-01";
	
	this.date = new caplin.presenter.node.CalendarDateField(enteredDate, startDate, endDate);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
