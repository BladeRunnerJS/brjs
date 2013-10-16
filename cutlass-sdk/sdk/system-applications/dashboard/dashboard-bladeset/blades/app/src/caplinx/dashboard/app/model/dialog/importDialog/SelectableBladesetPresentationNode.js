caplinx.dashboard.app.model.dialog.importDialog.SelectableBladesetPresentationNode = function(oPresentationModel, sBladeset, pBladePresentationNodes, fNewBladesetNameValidationListener)
{
	this.m_oPresentationModel = oPresentationModel;

	this.bladesetName = new caplin.presenter.property.Property(sBladeset);
	this.blades = new caplin.presenter.node.NodeList(pBladePresentationNodes,
		caplinx.dashboard.app.model.dialog.importDialog.SelectableBladePresentationNode);
	this.isSelected = new caplin.presenter.property.EditableProperty(true);
	this.isIndeterminate = new caplin.presenter.property.WritableProperty(false);
	
	this.displayNewBladesetField = new caplin.presenter.property.WritableProperty(true);
	this.newBladesetName = new caplinx.dashboard.app.model.form.Field("-- Please name your bladeset --", sBladeset);
	this.newBladesetName.value.addValidator(new caplinx.dashboard.app.model.dialog.validator.BladesetNameValidator(oPresentationModel));
	this.newBladesetName.value.addValidationCompleteListener(this, "_updateDialog");
	this.m_fNewBladesetNameValidationListener = fNewBladesetNameValidationListener;
	
	this.isSelected.addChangeListener(this, "_onSelectedChanged");
	this.blades.properties("isSelected").addChangeListener(this, "_onChildSelectedChanged");

	this.isSelected.setValue(false);
};
caplin.extend(caplinx.dashboard.app.model.dialog.importDialog.SelectableBladesetPresentationNode, caplin.presenter.node.PresentationNode);

caplinx.dashboard.app.model.dialog.importDialog.SelectableBladesetPresentationNode.prototype._onSelectedChanged = function()
{
	this.blades.properties("isSelected").setValue(this.isSelected.getValue());
	this.newBladesetName.value.forceValidation();
	this.displayNewBladesetField.setValue(this.isSelected.getValue());
};

caplinx.dashboard.app.model.dialog.importDialog.SelectableBladesetPresentationNode.prototype._onChildSelectedChanged = function()
{
	var nChildBladesSelected = this.blades.properties("isSelected", true).getSize();
	
	if(nChildBladesSelected == 0)
	{
		this.isSelected.setValue(false);
		this.isIndeterminate.setValue(false);
		this.displayNewBladesetField.setValue(false);
	}
	else if(nChildBladesSelected == this.blades.getPresentationNodesArray().length)
	{
		this.isSelected.setValue(true);
		this.isIndeterminate.setValue(false);
		this.displayNewBladesetField.setValue(true);
	}
	else
	{
		this.isIndeterminate.setValue(true);
		this.displayNewBladesetField.setValue(true);
	}
};

caplinx.dashboard.app.model.dialog.importDialog.SelectableBladesetPresentationNode.prototype._updateDialog = function()
{
	this.m_fNewBladesetNameValidationListener();
}
