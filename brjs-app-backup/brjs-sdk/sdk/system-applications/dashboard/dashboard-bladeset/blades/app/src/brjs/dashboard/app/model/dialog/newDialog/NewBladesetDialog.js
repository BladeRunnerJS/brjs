'use strict';

var Core = require('br/Core');
var DialogViewNode = require('brjs/dashboard/app/model/dialog/DialogViewNode');
var Button = require('br/presenter/node/Button');
var Field = require("brjs/dashboard/app/model/form/Field");
var BladesetNameValidator = require('brjs/dashboard/app/model/dialog/validator/BladesetNameValidator');

function NewBladesetDialog(oPresentationModel) {
	// call super constructor
	DialogViewNode.call(this, 'new-bladeset-dialog');

	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);

	this.bladesetName = new Field('-- Please name your bladeset --');
	this.createBladesetButton = new Button('Create');

	this.bladesetName.value.addValidator(new BladesetNameValidator(oPresentationModel));

	this.bladesetName.value.addValidationCompleteListener(this._updateDialog.bind(this));
}

Core.extend(NewBladesetDialog, DialogViewNode);

NewBladesetDialog.prototype.initializeForm = function() {
	this.bladesetName.value.setValue('');
	this.bladesetName.hasFocus.setValue(true);
	this._updateDialog();
};

NewBladesetDialog.prototype.createBladeset = function() {
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	this.m_oPresentationModel.getDashboardService().createBladeset(sApp,
		this.bladesetName.value.getValue(), this.m_fOnSuccess, this.m_fOnFailure);
	this.bladesetName.value.setValue('');
	this.m_oPresentationModel.dialog.visible.setValue(false);
};

NewBladesetDialog.prototype._updateDialog = function() {
	if (this.bladesetName.value.getValue() && !this.bladesetName.hasError.getValue()) {
		this.createBladesetButton.enabled.setValue(true);
	} else {
		this.createBladesetButton.enabled.setValue(false);
	}
};

NewBladesetDialog.prototype._onSuccess = function() {
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	this.m_oPresentationModel.appDetailScreen.displayApp(sApp);
};

NewBladesetDialog.prototype._onFailure = function(sFailureMessage) {
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};

module.exports = NewBladesetDialog;
