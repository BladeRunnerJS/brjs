brjs.dashboard.app.model.dialog.NotificationDialog = function(oPresentationModel)
{
	// call super constructor
	brjs.dashboard.app.model.dialog.DialogViewNode.call(this, "notification-dialog");
	
	this.m_oPresentationModel = oPresentationModel;
	
	this.message = new br.presenter.property.WritableProperty();
};
br.Core.extend(brjs.dashboard.app.model.dialog.NotificationDialog, brjs.dashboard.app.model.dialog.DialogViewNode);

brjs.dashboard.app.model.dialog.NotificationDialog.prototype.initializeForm = function()
{
	// do nothing
};
