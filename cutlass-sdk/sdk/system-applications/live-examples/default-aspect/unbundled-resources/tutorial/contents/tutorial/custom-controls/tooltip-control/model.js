novobank.example.DemoPresentationModel = function()
{
	this.txt = new caplin.presenter.node.ToolTipField("123");
	this.txt.value.addValidator(new caplin.core.validator.NumericValidator("error msg"));

	this.tooltip = new caplin.presenter.node.ToolTipNode();

	this.errorMonitor = new caplin.presenter.util.ErrorMonitor(this.tooltip);
	this.errorMonitor.monitorField(this.txt);
};
caplin.extend(novobank.example.DemoPresentationModel, caplin.presenter.PresentationModel);
