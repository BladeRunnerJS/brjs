caplinx.dashboard.app.model.dialog.importDialog.SelectableBladePresentationNode = function(sBlade)
{
	this.bladeName = new br.presenter.property.Property(sBlade);
	this.isSelected = new br.presenter.property.EditableProperty(true);
};
br.Core.extend(caplinx.dashboard.app.model.dialog.importDialog.SelectableBladePresentationNode, br.presenter.node.PresentationNode);
