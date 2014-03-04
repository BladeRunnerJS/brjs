caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog = function(oPresentationModel)
{
	// call super constructor
	caplinx.dashboard.app.model.dialog.DialogViewNode.call(this, "new-app-dialog");
	
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);
	
	this.appName = new caplinx.dashboard.app.model.form.Field("-- Please name your app --", "");
	this.appNamespace = new caplinx.dashboard.app.model.form.Field("-- Please give a namespace --");
	this.createAppButton = new br.presenter.node.Button("Create");
	
	this.appName.value.addValidator(new caplinx.dashboard.app.model.dialog.validator.AppNameValidator(oPresentationModel));
	this.appNamespace.value.addValidator(new caplinx.dashboard.app.model.dialog.validator.AppNamespaceValidator());
	
	this.appName.value.addValidationCompleteListener(this, "_updateDialog");
	this.appNamespace.value.addValidationCompleteListener(this, "_updateDialog");
	this.appNamespace.value.addChangeListener(this, "_onNamespaceChanged");
};
br.Core.extend(caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog, caplinx.dashboard.app.model.dialog.DialogViewNode);

caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog.prototype.initializeForm = function()
{
	this.appName.value.setValue("");
	this.appName.hasFocus.setValue(true);
	this.appNamespace.value.setValue(this.m_oPresentationModel.dialog.getAppNamespace());
	this._updateDialog();
};

caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog.prototype.createApp = function()
{
	this.m_oPresentationModel.getDashboardService().createApp(
		this.appName.value.getValue(), this.appNamespace.value.getValue(), this.m_fOnSuccess, this.m_fOnFailure);
	this.appName.value.setValue("");
	this.m_oPresentationModel.dialog.visible.setValue(false);
};

caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog.prototype._updateDialog = function()
{
	this._updateNamespace();
	this._updateCreateButton();
};

caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog.prototype._updateNamespace = function()
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

caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog.prototype._updateCreateButton = function()
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

caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog.prototype._onNamespaceChanged = function()
{
	this.m_oPresentationModel.dialog.setAppNamespace(this.appNamespace.value.getValue());
};

caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog.prototype._onSuccess = function()
{
	this.m_oPresentationModel.appsScreen.displayApps();
};

caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog.prototype._onFailure = function(sFailureMessage)
{
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};
