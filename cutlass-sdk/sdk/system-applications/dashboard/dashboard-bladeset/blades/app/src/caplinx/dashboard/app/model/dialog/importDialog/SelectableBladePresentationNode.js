caplinx.dashboard.app.model.dialog.importDialog.SelectableBladePresentationNode = function(sBlade)
{
	this.bladeName = new caplin.presenter.property.Property(sBlade);
	this.isSelected = new caplin.presenter.property.EditableProperty(true);
};
caplin.extend(caplinx.dashboard.app.model.dialog.importDialog.SelectableBladePresentationNode, caplin.presenter.node.PresentationNode);
