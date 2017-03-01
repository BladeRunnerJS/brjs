'use strict';

var Core = require('br/Core');
var Button = require('br/presenter/node/Button');
var Field = require("brjs/dashboard/app/model/form/Field");
var DialogViewNode = require('brjs/dashboard/app/model/dialog/DialogViewNode');
var BladeNameValidator = require('brjs/dashboard/app/model/dialog/validator/BladeNameValidator');

function NewBladeDialog(oPresentationModel) {
	// call super constructor
	DialogViewNode.call(this, 'new-blade-dialog');

	this.m_oPresentationModel = oPresentationModel;
	this.m_oBladeNameValidator = new BladeNameValidator(oPresentationModel);
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);

	this.bladeName = new Field('-- Please name your blade --');
	this.createBladeButton = new Button('Create');

	this.bladeName.value.addValidator(this.m_oBladeNameValidator);

	this.bladeName.value.addValidationCompleteListener(this._updateDialog.bind(this));
}

Core.extend(NewBladeDialog, DialogViewNode);

NewBladeDialog.prototype.initializeForm = function() {
	this.bladeName.value.setValue('');
	this.bladeName.hasFocus.setValue(true);
	this._updateDialog();
};

NewBladeDialog.prototype.setBladeset = function(sBladeset) {
	this.m_sBladeset = sBladeset;
	this.m_oBladeNameValidator.setBladeset(sBladeset);
};

NewBladeDialog.prototype.createBlade = function() {
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	this.m_oPresentationModel.getDashboardService().createBlade(sApp, this.m_sBladeset,
		this.bladeName.value.getValue(), this.m_fOnSuccess, this.m_fOnFailure);
	this.bladeName.value.setValue('');
	this.m_oPresentationModel.dialog.visible.setValue(false);
};

NewBladeDialog.prototype._updateDialog = function() {
	if (this.bladeName.value.getValue() && !this.bladeName.hasError.getValue()) {
		this.createBladeButton.enabled.setValue(true);
	} else {
		this.createBladeButton.enabled.setValue(false);
	}
};

NewBladeDialog.prototype._onSuccess = function() {
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	this.m_oPresentationModel.appDetailScreen.displayApp(sApp);
};

NewBladeDialog.prototype._onFailure = function(sFailureMessage) {
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};

module.exports = NewBladeDialog;
