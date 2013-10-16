novobank.example.DemoPresentationModel = function()
{
	this.date = new caplin.presenter.node.DateField("2012-03-21");
	// N.B. It is also possible to use a native JavaScript Date object to construct a DateField
	// The below example uses the current date using this method, try uncommenting it:
	//this.date = new caplin.presenter.node.DateField(new Date());
	this.date.label.setValue("Date");

	this.rangedDate = new caplin.presenter.node.DateField("2012-03-21", "2012-01-01", "2012-12-30");
	this.rangedDate.label.setValue("Ranged Date");
};
caplin.implement(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
