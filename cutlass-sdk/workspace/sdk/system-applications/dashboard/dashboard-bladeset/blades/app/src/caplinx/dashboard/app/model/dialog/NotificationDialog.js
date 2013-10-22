caplinx.dashboard.app.model.dialog.NotificationDialog = function(oPresentationModel)
{
	// call super constructor
	caplinx.dashboard.app.model.dialog.DialogViewNode.call(this, "notification-dialog");
	
	this.m_oPresentationModel = oPresentationModel;
	
	this.message = new caplin.presenter.property.WritableProperty();
};
caplin.extend(caplinx.dashboard.app.model.dialog.NotificationDialog, caplinx.dashboard.app.model.dialog.DialogViewNode);

caplinx.dashboard.app.model.dialog.NotificationDialog.prototype.initializeForm = function()
{
	// do nothing
};
