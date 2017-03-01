'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');

function ReleaseNoteScreen(oPresentationModel) {
	this.m_oPresentationModel = oPresentationModel;
	this.m_fRefreshReleaseNote = this._refreshReleaseNoteScreen.bind(this);
	this.m_fOnServiceError = this._onServiceError.bind(this);

	this.visible = new WritableProperty(false);
	this.content = new WritableProperty();
}

Core.extend(ReleaseNoteScreen, PresentationNode);

ReleaseNoteScreen.prototype.displayReleaseNote = function() {
	this.m_oPresentationModel.setCurrentSection('support');
	this.m_oPresentationModel.getDashboardService().getCurrentReleaseNote(this.m_fRefreshReleaseNote, this.m_fOnServiceError);
};

ReleaseNoteScreen.prototype._refreshReleaseNoteScreen = function(sReleaseNote) {
	this.m_oPresentationModel.hideAllScreens();
	this.visible.setValue(true);
	this.content.setValue(sReleaseNote);
};

ReleaseNoteScreen.prototype._onServiceError = function(sErrorMessage) {
	this.m_oPresentationModel.dialog.displayNotification(sErrorMessage);
};

module.exports = ReleaseNoteScreen;
