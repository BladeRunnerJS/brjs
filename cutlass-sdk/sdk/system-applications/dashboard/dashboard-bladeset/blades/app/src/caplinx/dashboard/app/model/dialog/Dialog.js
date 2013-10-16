caplin.thirdparty("wolf-simple-box");

caplinx.dashboard.app.model.dialog.Dialog = function(oPresentationModel)
{
	this.visible = new caplin.presenter.property.WritableProperty(false);
	this.visible.addChangeListener(this, "_onVisibilityChanged");
	this.type = new caplin.presenter.property.Property(null);
	this.isClosable = new caplin.presenter.property.EditableProperty(true);
	this.viewNode = new caplin.presenter.node.MappedNodeList({}, caplinx.dashboard.app.model.dialog.DialogViewNode);
	this.m_oModal = new WolfSimpleBox();
	this.m_oModal.callOnClose( this._onClose.bind( this ) );
	this.m_oPresentationModel = oPresentationModel;
	this.m_sAppNamespace = "";
};
caplin.extend(caplinx.dashboard.app.model.dialog.Dialog, caplin.presenter.node.PresentationNode);

caplinx.dashboard.app.model.dialog.Dialog.prototype.initialize = function()
{
	this.newAppDialog = new caplinx.dashboard.app.model.dialog.newDialog.NewAppDialog(this.m_oPresentationModel);
	this.newBladesetDialog = new caplinx.dashboard.app.model.dialog.newDialog.NewBladesetDialog(this.m_oPresentationModel);
	this.newBladeDialog = new caplinx.dashboard.app.model.dialog.newDialog.NewBladeDialog(this.m_oPresentationModel);
	this.importMotifDialog = new caplinx.dashboard.app.model.dialog.importDialog.ImportMotifDialog(this.m_oPresentationModel);
	this.importBladesFromAppDialog = new caplinx.dashboard.app.model.dialog.importDialog.ImportBladesFromAppDialog(this.m_oPresentationModel);
	this.testRunnerDialog = new caplinx.dashboard.app.model.dialog.TestRunnerDialog(this.m_oPresentationModel);
	this.notificationDialog = new caplinx.dashboard.app.model.dialog.NotificationDialog(this.m_oPresentationModel);
	this.browserWarningDialog = new caplinx.dashboard.app.model.dialog.BrowserWarningDialog(this.m_oPresentationModel);
};

caplinx.dashboard.app.model.dialog.Dialog.prototype.showDialog = function(sDialog)
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

caplinx.dashboard.app.model.dialog.Dialog.prototype.displayNotification = function(sMessage)
{
	var htmlMessage = sMessage.replace(/\n/g, '<br/>');
	
	this.notificationDialog.message.setValue(htmlMessage);
	this.showDialog("notificationDialog");
};

caplinx.dashboard.app.model.dialog.Dialog.prototype.getAppNamespace = function()
{
	return this.m_sAppNamespace;
};

caplinx.dashboard.app.model.dialog.Dialog.prototype.setAppNamespace = function(sAppNamespace)
{
	this.m_sAppNamespace = sAppNamespace;
};

caplinx.dashboard.app.model.dialog.Dialog.prototype._onVisibilityChanged = function(sDialogClass)
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

caplinx.dashboard.app.model.dialog.Dialog.prototype._openDialog = function(bPreventClose)
{
	this.m_oModal.setClosable( this.isClosable.getValue() );
	this.m_oModal.show();
};

caplinx.dashboard.app.model.dialog.Dialog.prototype._onClose = function(sDialogClass)
{
	if(this.m_oPresentationModel)
	{
		this.m_oPresentationModel.appsScreen.updateApps();
		this.visible.setValue(false);
	}
};


