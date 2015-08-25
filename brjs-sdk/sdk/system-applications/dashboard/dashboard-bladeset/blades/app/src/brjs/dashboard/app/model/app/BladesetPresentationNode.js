'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var NodeList = require('br/presenter/node/NodeList');
var EditableProperty = require('br/presenter/property/EditableProperty');
var Property = require('br/presenter/property/Property');
var WritableProperty = require('br/presenter/property/WritableProperty');
var BladePresentationNode = require("brjs/dashboard/app/model/app/BladePresentationNode");

function BladesetPresentationNode(sBladesetName, pBlades, oPresentationModel) {
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnTestProgress = this._onTestProgress.bind(this);
	this.m_fOnTestError = this._onTestError.bind(this);
	this.m_oHoverTimeout = null;
	this.workbenchUrl = new WritableProperty();
	this.isBladeset = new WritableProperty(false);

	this.bladesetName = new Property(sBladesetName);
	this.bladeSetClasses = new EditableProperty('bladeset');
	this.blades = new NodeList(this._getBladePresentationModels(pBlades || []),
		BladePresentationNode);
	if (this.m_oPresentationModel && sBladesetName != 'default') {
		this.isBladeset.setValue(true);
		var appName = this.m_oPresentationModel.appDetailScreen.appName.getValue();
		var sWorkbenchPopoutUrl = '/' + appName + '/' + sBladesetName + '/workbench/';
		this.workbenchUrl.setValue(sWorkbenchPopoutUrl);
	}
}

Core.extend(BladesetPresentationNode, PresentationNode);

BladesetPresentationNode.prototype.getBlade = function(sBlade) {
	var pNodes = this.blades.nodes('*', {
		bladeName: sBlade
	}).getNodesArray();

	return (pNodes.length == 1) ? pNodes[0] : null;
};

BladesetPresentationNode.prototype.newBlade = function() {
	this.m_oPresentationModel.dialog.newBladeDialog.setBladeset(this.bladesetName.getValue());
	this.m_oPresentationModel.dialog.showDialog('newBladeDialog');
};

BladesetPresentationNode.prototype.runAllTests = function() {
	if (this.m_oPresentationModel.getDashboardService().getTestRunInProgress()) {
		this.m_oPresentationModel.dialog.displayNotification("Can't run tests. The test run is in progress.");
		return;
	}
	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(true);

	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	var sBladeset = this.bladesetName.getValue();
	this.m_oPresentationModel.dialog.displayNotification('Running tests... (this may take a few minutes). Please wait for test results.');
	this.m_oPresentationModel.getDashboardService().runBladesetTests(sApp, sBladeset, this.m_fOnTestProgress, this.m_fOnTestError);
};

BladesetPresentationNode.prototype._onTestProgress = function(sTestOutput) {
	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(false);
	this.m_oPresentationModel.dialog.showDialog('testRunnerDialog');
	this.m_oPresentationModel.dialog.testRunnerDialog.displayTestResults();
};

BladesetPresentationNode.prototype._onTestError = function(sErrorMessage) {
	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(false);
	this.m_oPresentationModel.dialog.showDialog('testRunnerDialog');
	this.m_oPresentationModel.dialog.testRunnerDialog.displayTestError(sErrorMessage);
};

BladesetPresentationNode.prototype.popoutWorkbench = function() {
	this.m_oPresentationModel.getWindowOpenerService().openWindow(this.workbenchUrl.getValue());
};

BladesetPresentationNode.prototype._getBladePresentationModels = function(pBlades) {
	var pBladePresentationModels = [];

	for (var i = 0, l = pBlades.length; i < l; ++i) {
		var sBladeName = pBlades[i];
		var oBladePresentationModel = new BladePresentationNode(sBladeName, this,
			this.m_oPresentationModel);

		pBladePresentationModels.push(oBladePresentationModel);
	}

	return pBladePresentationModels;
};

BladesetPresentationNode.prototype.onBladesetMouseOver = function() {
	clearTimeout(this.m_oHoverTimeout);
	this.bladeSetClasses.setValue('bladeset hover');
};

BladesetPresentationNode.prototype.onBladesetMouseOut = function() {
	this.m_oHoverTimeout = setTimeout(this._removeHoverClass.bind(this), 150);
};

BladesetPresentationNode.prototype._removeHoverClass = function() {
	this.bladeSetClasses.setValue('bladeset');
};

module.exports = BladesetPresentationNode;
