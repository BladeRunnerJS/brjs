'use strict';

var jQuery = require('jquery');
var Core = require('br/Core');
var DialogViewNode = require('brjs/dashboard/app/model/dialog/DialogViewNode');
var WritableProperty = require('br/presenter/property/WritableProperty');
var Button = require('br/presenter/node/Button');
var FileField = require("brjs/dashboard/app/model/form/FileField");
var Field = require("brjs/dashboard/app/model/form/Field");
var AppNameValidator = require("brjs/dashboard/app/model/dialog/validator/AppNameValidator");
var AppNamespaceValidator = require("brjs/dashboard/app/model/dialog/validator/AppNamespaceValidator");

function ImportMotifDialog(oPresentationModel) {
	// call super constructor
	DialogViewNode.call(this, 'import-motif-dialog');

	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);

	this.appZip = new FileField('application/zip');
	this.appName = new Field('-- Please name your app --', '');
	this.appNamespace = new Field('-- Please give a namespace --');
	this.createAppButton = new Button('Create');
	this.formVisible = new WritableProperty(true);
	this.processingVisible = new WritableProperty(false);

	this.appName.value.addValidator(new AppNameValidator(oPresentationModel));
	this.appNamespace.value.addValidator(new AppNamespaceValidator());

	this.appZip.fileSelected.addChangeListener(this, '_updateDialog');
	this.appName.value.addValidationCompleteListener(this, '_updateDialog');
	this.appNamespace.value.addValidationCompleteListener(this, '_updateDialog');
	this.appNamespace.value.addChangeListener(this, '_onNamespaceChanged');
}

Core.extend(ImportMotifDialog, DialogViewNode);

ImportMotifDialog.prototype.initializeForm = function() {
	this.appName.value.setValue('');
	this.appNamespace.value.setValue(this.m_oPresentationModel.dialog.getAppNamespace());
	this._updateDialog();
};

ImportMotifDialog.prototype.importMotif = function() {
	this.m_oPresentationModel.getDashboardService().importMotif(this.appName.value.getValue(),
		this.appNamespace.value.getValue(), this.appZip.getFileInput().files[0], this.m_fOnSuccess, this.m_fOnFailure);
	this.formVisible.setValue(false);
	this.processingVisible.setValue(true);
};

ImportMotifDialog.prototype._updateDialog = function() {
	this._updateAppName();
	this._updateNamespace();
	this._updateCreateButton();
};

ImportMotifDialog.prototype._updateAppName = function() {
	if (this.appZip.fileSelected.getValue()) {
		this.appName.enabled.setValue(true);
	} else {
		this.appName.enabled.setValue(false);
	}
};

ImportMotifDialog.prototype._updateNamespace = function() {
	if (this.appName.value.getValue()) {
		this.appNamespace.enabled.setValue(true);
	} else {
		this.appNamespace.enabled.setValue(false);
	}
};

ImportMotifDialog.prototype._updateCreateButton = function() {
	if (this.appName.value.getValue() && !this.appName.hasError.getValue() &&
		this.appNamespace.value.getValue() && !this.appNamespace.hasError.getValue()) {
		this.createAppButton.enabled.setValue(true);
	} else {
		this.createAppButton.enabled.setValue(false);
	}
};

ImportMotifDialog.prototype._onNamespaceChanged = function() {
	this.m_oPresentationModel.dialog.setAppNamespace(this.appNamespace.value.getValue());
};

ImportMotifDialog.prototype._onSuccess = function() {
	this.m_oPresentationModel.appsScreen.displayApps();
	this.m_oPresentationModel.dialog.visible.setValue(false);
	this.formVisible.setValue(true);
	this.processingVisible.setValue(false);
	this.appZip.chooseDifferentFile(null);
	jQuery('#input_motif_button input').attr('value', '');
};

ImportMotifDialog.prototype._onFailure = function(sFailureMessage) {
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};

module.exports = ImportMotifDialog;
