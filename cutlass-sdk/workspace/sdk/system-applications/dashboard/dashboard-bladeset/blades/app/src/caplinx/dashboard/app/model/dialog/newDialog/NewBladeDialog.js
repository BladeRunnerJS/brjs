caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog = function(oPresentationModel)
{
	// call super constructor
	caplinx.dashboard.app.model.dialog.DialogViewNode.call(this, "new-blade-dialog");
	
	this.m_oPresentationModel = oPresentationModel;
	this.m_oBladeNameValidator = new caplinx.dashboard.app.model.dialog.validator.BladeNameValidator(oPresentationModel);
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);
	
	this.bladeName = new caplinx.dashboard.app.model.form.Field("-- Please name your blade --");
	this.createBladeButton = new br.presenter.node.Button("Create");
	
	this.bladeName.value.addValidator(this.m_oBladeNameValidator);
	
	this.bladeName.value.addValidationCompleteListener(this, "_updateDialog");
};
br.extend(caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog, caplinx.dashboard.app.model.dialog.DialogViewNode);

caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog.prototype.initializeForm = function()
{
	this.bladeName.value.setValue("");
	this.bladeName.hasFocus.setValue(true);
	this._updateDialog();
};

caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog.prototype.setBladeset = function(sBladeset)
{
	this.m_sBladeset = sBladeset;
	this.m_oBladeNameValidator.setBladeset(sBladeset);
};

caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog.prototype.createBlade = function()
{
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	this.m_oPresentationModel.getDashboardService().createBlade(sApp, this.m_sBladeset,
		this.bladeName.value.getValue(), this.m_fOnSuccess, this.m_fOnFailure);
	this.bladeName.value.setValue("");
	this.m_oPresentationModel.dialog.visible.setValue(false);
};

caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog.prototype._updateDialog = function()
{
	if(this.bladeName.value.getValue() && !this.bladeName.hasError.getValue())
	{
		this.createBladeButton.enabled.setValue(true);
	}
	else
	{
		this.createBladeButton.enabled.setValue(false);
	}
};

caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog.prototype._onSuccess = function()
{
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	this.m_oPresentationModel.appDetailScreen.displayApp(sApp);
};

caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog.prototype._onFailure = function(sFailureMessage)
{
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};
