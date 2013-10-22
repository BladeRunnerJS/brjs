caplinx.dashboard.app.model.dialog.NotificationDialog = function(oPresentationModel)
{
	// call super constructor
	caplinx.dashboard.app.model.dialog.DialogViewNode.call(this, "notification-dialog");
	
	this.m_oPresentationModel = oPresentationModel;
	
	this.message = new br.presenter.property.WritableProperty();
};
br.extend(caplinx.dashboard.app.model.dialog.NotificationDialog, caplinx.dashboard.app.model.dialog.DialogViewNode);

caplinx.dashboard.app.model.dialog.NotificationDialog.prototype.initializeForm = function()
{
	// do nothing
};
