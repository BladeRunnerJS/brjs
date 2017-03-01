'use strict';

var Core = require('br/Core');
var Button = require('br/presenter/node/Button');
var DialogViewNode = require("brjs/dashboard/app/model/dialog/DialogViewNode");
var Field = require("brjs/dashboard/app/model/form/Field");
var AppNameValidator = require("brjs/dashboard/app/model/dialog/validator/AppNameValidator");
var AppNamespaceValidator = require("brjs/dashboard/app/model/dialog/validator/AppNamespaceValidator");

function NewAppDialog(oPresentationModel) {
	// call super constructor
	DialogViewNode.call(this, 'new-app-dialog');

	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);

	this.appName = new Field('-- Please name your app --', '');
	this.appNamespace = new Field('-- Please give a namespace --');
	this.createAppButton = new Button('Create');

	this.appName.value.addValidator(new AppNameValidator(oPresentationModel));
	this.appNamespace.value.addValidator(new AppNamespaceValidator());

	this.appName.value.addValidationCompleteListener(this._updateDialog.bind(this));
	this.appNamespace.value.addValidationCompleteListener(this._updateDialog.bind(this));
	this.appNamespace.value.addChangeListener(this._onNamespaceChanged.bind(this));
}

Core.extend(NewAppDialog, DialogViewNode);

NewAppDialog.prototype.initializeForm = function() {
	this.appName.value.setValue('');
	this.appName.hasFocus.setValue(true);
	this.appNamespace.value.setValue(this.m_oPresentationModel.dialog.getAppNamespace());
	this._updateDialog();
};

NewAppDialog.prototype.createApp = function() {
	this.m_oPresentationModel.getDashboardService().createApp(
		this.appName.value.getValue(), this.appNamespace.value.getValue(), this.m_fOnSuccess, this.m_fOnFailure);
	this.appName.value.setValue('');
	this.m_oPresentationModel.dialog.visible.setValue(false);
};

NewAppDialog.prototype._updateDialog = function() {
	this._updateNamespace();
	this._updateCreateButton();
};

NewAppDialog.prototype._updateNamespace = function() {
	if (this.appName.value.getValue()) {
		this.appNamespace.enabled.setValue(true);
	} else {
		this.appNamespace.enabled.setValue(false);
	}
};

NewAppDialog.prototype._updateCreateButton = function() {
	if (this.appName.value.getValue() && !this.appName.hasError.getValue() &&
		this.appNamespace.value.getValue() && !this.appNamespace.hasError.getValue()) {
		this.createAppButton.enabled.setValue(true);
	} else {
		this.createAppButton.enabled.setValue(false);
	}
};

NewAppDialog.prototype._onNamespaceChanged = function() {
	this.m_oPresentationModel.dialog.setAppNamespace(this.appNamespace.value.getValue());
};

NewAppDialog.prototype._onSuccess = function() {
	this.m_oPresentationModel.appsScreen.displayApps();
};

NewAppDialog.prototype._onFailure = function(sFailureMessage) {
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};

module.exports = NewAppDialog;
