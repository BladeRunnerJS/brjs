caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog = function(oPresentationModel)
{
	// call super constructor
	caplinx.dashboard.app.model.dialog.DialogViewNode.call(this, "import-motif-dialog");
	
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);
	
	this.appZip = new caplinx.dashboard.app.model.form.FileField("application/zip");
	this.appName = new caplinx.dashboard.app.model.form.Field("-- Please name your app --", "");
	this.appNamespace = new caplinx.dashboard.app.model.form.Field("-- Please give a namespace --");
	this.createAppButton = new br.presenter.node.Button("Create");
	this.formVisible = new br.presenter.property.WritableProperty( true );
	this.processingVisible = new br.presenter.property.WritableProperty( false );
	
	this.appName.value.addValidator(new caplinx.dashboard.app.model.dialog.validator.AppNameValidator(oPresentationModel));
	this.appNamespace.value.addValidator(new caplinx.dashboard.app.model.dialog.validator.AppNamespaceValidator());
	
	this.appZip.fileSelected.addChangeListener(this, "_updateDialog");
	this.appName.value.addValidationCompleteListener(this, "_updateDialog");
	this.appNamespace.value.addValidationCompleteListener(this, "_updateDialog");
	this.appNamespace.value.addChangeListener(this, "_onNamespaceChanged");
};
br.Core.extend(caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog, caplinx.dashboard.app.model.dialog.DialogViewNode);

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype.initializeForm = function()
{
	this.appName.value.setValue("");
	this.appNamespace.value.setValue(this.m_oPresentationModel.dialog.getAppNamespace());
	this._updateDialog();
};

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype.importMotif = function()
{
	this.m_oPresentationModel.getDashboardService().importMotif(this.appName.value.getValue(),
	this.appNamespace.value.getValue(), this.appZip.getFileInput().files[0], this.m_fOnSuccess, this.m_fOnFailure);
	this.formVisible.setValue( false );
	this.processingVisible.setValue( true );
};

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype._updateDialog = function()
{
	this._updateAppName();
	this._updateNamespace();
	this._updateCreateButton();
};

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype._updateAppName = function()
{
	if(this.appZip.fileSelected.getValue())
	{
		this.appName.enabled.setValue(true);
	}
	else
	{
		this.appName.enabled.setValue(false);
	}
};

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype._updateNamespace = function()
{
	if(this.appName.value.getValue())
	{
		this.appNamespace.enabled.setValue(true);
	}
	else
	{
		this.appNamespace.enabled.setValue(false);
	}
};

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype._updateCreateButton = function()
{
	if(this.appName.value.getValue() && !this.appName.hasError.getValue() &&
		this.appNamespace.value.getValue() && !this.appNamespace.hasError.getValue())
	{
		this.createAppButton.enabled.setValue(true);
	}
	else
	{
		this.createAppButton.enabled.setValue(false);
	}
};

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype._onNamespaceChanged = function()
{
	this.m_oPresentationModel.dialog.setAppNamespace(this.appNamespace.value.getValue());
};

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype._onSuccess = function()
{
	this.m_oPresentationModel.appsScreen.displayApps();
	this.m_oPresentationModel.dialog.visible.setValue(false);
	this.formVisible.setValue( true );
	this.processingVisible.setValue( false );
	this.appZip.chooseDifferentFile( null );
	jQuery("#input_motif_button input").attr("value", "");
};

caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog.prototype._onFailure = function(sFailureMessage)
{
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
	this.m_oPresentationModel.dialog.visible.setValue(false);
};
