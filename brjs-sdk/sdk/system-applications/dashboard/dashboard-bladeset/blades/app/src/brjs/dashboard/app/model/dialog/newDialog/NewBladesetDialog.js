brjs.dashboard.app.model.dialog.newDialog.NewBladesetDialog = function(oPresentationModel)
{
	// call super constructor
	brjs.dashboard.app.model.dialog.DialogViewNode.call(this, "new-bladeset-dialog");
	
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);
	
	this.bladesetName = new brjs.dashboard.app.model.form.Field("-- Please name your bladeset --");
	this.createBladesetButton = new br.presenter.node.Button("Create");
	
	this.bladesetName.value.addValidator(new brjs.dashboard.app.model.dialog.validator.BladesetNameValidator(oPresentationModel));
	
	this.bladesetName.value.addValidationCompleteListener(this, "_updateDialog");
};
br.Core.extend(brjs.dashboard.app.model.dialog.newDialog.NewBladesetDialog, brjs.dashboard.app.model.dialog.DialogViewNode);

brjs.dashboard.app.model.dialog.newDialog.NewBladesetDialog.prototype.initializeForm = function()
{
	this.bladesetName.value.setValue("");
	this.bladesetName.hasFocus.setValue(true);
	this._updateDialog();
};

brjs.dashboard.app.model.dialog.newDialog.NewBladesetDialog.prototype.createBladeset = function()
{
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	this.m_oPresentationModel.getDashboardService().createBladeset(sApp,
		this.bladesetName.value.getValue(), this.m_fOnSuccess, this.m_fOnFailure);
	this.bladesetName.value.setValue("");
	this.m_oPresentationModel.dialog.visible.setValue(false);
};

brjs.dashboard.app.model.dialog.newDialog.NewBladesetDialog.prototype._updateDialog = function()
{
	if(this.bladesetName.value.getValue() && !this.bladesetName.hasError.getValue())
	{
		this.createBladesetButton.enabled.setValue(true);
	}
	else
	{
		this.createBladesetButton.enabled.setValue(false);
	}
};

brjs.dashboard.app.model.dialog.newDialog.NewBladesetDialog.prototype._onSuccess = function()
{
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	this.m_oPresentationModel.appDetailScreen.displayApp(sApp);
};

brjs.dashboard.app.model.dialog.newDialog.NewBladesetDialog.prototype._onFailure = function(sFailureMessage)
{
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};
