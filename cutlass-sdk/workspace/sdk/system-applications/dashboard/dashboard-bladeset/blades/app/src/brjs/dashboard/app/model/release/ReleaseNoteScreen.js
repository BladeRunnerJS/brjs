brjs.dashboard.app.model.release.ReleaseNoteScreen = function(oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
	this.m_fRefreshReleaseNote = this._refreshReleaseNoteScreen.bind(this);
	this.m_fOnServiceError = this._onServiceError.bind(this);
	
	this.visible = new br.presenter.property.WritableProperty(false);
	this.content = new br.presenter.property.WritableProperty();
};
br.Core.extend(brjs.dashboard.app.model.release.ReleaseNoteScreen, br.presenter.node.PresentationNode);

brjs.dashboard.app.model.release.ReleaseNoteScreen.prototype.displayReleaseNote = function()
{
	this.m_oPresentationModel.setCurrentSection("support");
	this.m_oPresentationModel.getDashboardService().getCurrentReleaseNote(this.m_fRefreshReleaseNote, this.m_fOnServiceError);
};

brjs.dashboard.app.model.release.ReleaseNoteScreen.prototype._refreshReleaseNoteScreen = function(sReleaseNote)
{
	this.m_oPresentationModel.hideAllScreens();
	this.visible.setValue(true);
	this.content.setValue(sReleaseNote);
};

brjs.dashboard.app.model.release.ReleaseNoteScreen.prototype._onServiceError = function(sErrorMessage)
{
	this.m_oPresentationModel.dialog.displayNotification(sErrorMessage);
};
