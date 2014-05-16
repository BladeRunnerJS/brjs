br.Core.thirdparty("wolf-simple-box");

brjs.dashboard.app.model.dialog.Dialog = function(oPresentationModel)
{
	this.visible = new br.presenter.property.WritableProperty(false);
	this.visible.addChangeListener(this, "_onVisibilityChanged");
	this.type = new br.presenter.property.Property(null);
	this.isClosable = new br.presenter.property.EditableProperty(true);
	this.viewNode = new br.presenter.node.MappedNodeList({}, brjs.dashboard.app.model.dialog.DialogViewNode);
	this.m_oModal = new WolfSimpleBox();
	this.m_oModal.callOnClose( this._onClose.bind( this ) );
	this.m_oPresentationModel = oPresentationModel;
	this.m_sAppNamespace = "";
};
br.Core.extend(brjs.dashboard.app.model.dialog.Dialog, br.presenter.node.PresentationNode);

brjs.dashboard.app.model.dialog.Dialog.prototype.initialize = function()
{
	this.newAppDialog = new brjs.dashboard.app.model.dialog.newDialog.NewAppDialog(this.m_oPresentationModel);
	this.newBladesetDialog = new brjs.dashboard.app.model.dialog.newDialog.NewBladesetDialog(this.m_oPresentationModel);
	this.newBladeDialog = new brjs.dashboard.app.model.dialog.newDialog.NewBladeDialog(this.m_oPresentationModel);
	this.importMotifDialog = new brjs.dashboard.app.model.dialog.importDialog.ImportMotifDialog(this.m_oPresentationModel);
	this.importBladesFromAppDialog = new brjs.dashboard.app.model.dialog.importDialog.ImportBladesFromAppDialog(this.m_oPresentationModel);
	this.testRunnerDialog = new brjs.dashboard.app.model.dialog.TestRunnerDialog(this.m_oPresentationModel);
	this.notificationDialog = new brjs.dashboard.app.model.dialog.NotificationDialog(this.m_oPresentationModel);
	this.browserWarningDialog = new brjs.dashboard.app.model.dialog.BrowserWarningDialog(this.m_oPresentationModel);
};

brjs.dashboard.app.model.dialog.Dialog.prototype.showDialog = function(sDialog)
{
	if( this.m_oModal.hasContent() === false )
	{
		this.m_oModal.setContent( $('#modalDialog') );
	}

	
	var oDialog = this[sDialog];
	this.visible.setValue(true);
	oDialog.initializeForm();
	this.m_oModal.setHasBackground( oDialog.hasBackground.getValue() );
	this.isClosable.setValue(oDialog.isClosable.getValue());

	this.type._$setInternalValue(sDialog);
	this.viewNode.updateList({current:oDialog});
};

brjs.dashboard.app.model.dialog.Dialog.prototype.displayNotification = function(sMessage)
{
	var htmlMessage = sMessage.replace(/\n/g, '<br/>');
	
	this.notificationDialog.message.setValue(htmlMessage);
	this.showDialog("notificationDialog");
};

brjs.dashboard.app.model.dialog.Dialog.prototype.getAppNamespace = function()
{
	return this.m_sAppNamespace;
};

brjs.dashboard.app.model.dialog.Dialog.prototype.setAppNamespace = function(sAppNamespace)
{
	this.m_sAppNamespace = sAppNamespace;
};

brjs.dashboard.app.model.dialog.Dialog.prototype._onVisibilityChanged = function(sDialogClass)
{
	if(this.visible.getValue() === true)
	{
		this._openDialog();
	}
	else
	{
		this.m_oModal.hide();
	}
};

brjs.dashboard.app.model.dialog.Dialog.prototype._openDialog = function(bPreventClose)
{
	this.m_oModal.setClosable( this.isClosable.getValue() );
	this.m_oModal.show();
};

brjs.dashboard.app.model.dialog.Dialog.prototype._onClose = function(sDialogClass)
{
	if(this.m_oPresentationModel)
	{
		this.m_oPresentationModel.appsScreen.updateApps();
		this.visible.setValue(false);
	}
};


