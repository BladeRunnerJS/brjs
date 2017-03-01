'use strict';

var Core = require('br/Core');
var EditableProperty = require('br/presenter/property/EditableProperty');
var Field = require('br/presenter/node/Field');
var DialogViewNode = require("brjs/dashboard/app/model/dialog/DialogViewNode");

function TestRunnerDialog(oPresentationModel) {
	// call super constructor
	DialogViewNode.call(this, 'test-runner-dialog');

	this.m_oPresentationModel = oPresentationModel;

	var sTestResultsUrl = oPresentationModel.getDashboardService().getTestResultsUrl();
	this.testResultsLink = new Field(sTestResultsUrl);
	this.errorMessage = new Field('');
	this.testsPassed = new EditableProperty(false);
}

Core.extend(TestRunnerDialog, DialogViewNode);

TestRunnerDialog.prototype.initializeForm = function() {
	this.testResultsLink.visible.setValue(false);
	this.errorMessage.value.setValue('');
	this.errorMessage.visible.setValue(false);
	this.testsPassed.setValue(false);
};

TestRunnerDialog.prototype.displayTestResults = function() {
	this.testResultsLink.visible.setValue(true);
	this.testsPassed.setValue(true);
};

TestRunnerDialog.prototype.displayTestError = function(sErrorMessage) {
	this.testResultsLink.visible.setValue(true);
	this.errorMessage.value.setValue(sErrorMessage + ' Check BladeRunner console for error messages.');
	this.errorMessage.visible.setValue(true);
};

TestRunnerDialog.prototype.closeDialog = function() {
	this.m_oPresentationModel.dialog.visible.setValue(false);
};

module.exports = TestRunnerDialog;
