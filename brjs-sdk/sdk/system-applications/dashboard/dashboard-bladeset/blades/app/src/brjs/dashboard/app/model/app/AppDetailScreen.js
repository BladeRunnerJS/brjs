'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var BladesetPresentationNode = require('brjs/dashboard/app/model/app/BladesetPresentationNode');
var Core = require('br/Core');
var NodeList = require('br/presenter/node/NodeList');
var Button = require('brjs/dashboard/app/model/form/Button');
var WritableProperty = require('br/presenter/property/WritableProperty');
var ConditionalChangeListener = require("brjs/dashboard/app/model/ConditionalChangeListener");

function AppDetailScreen(oPresentationModel) {
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnServiceError = this._onServiceError.bind(this);

	this.visible = new WritableProperty(false);
	this.appName = new WritableProperty();
	this.bladesets = new NodeList([], BladesetPresentationNode);

	this.newBladesetButton = new Button('New Bladeset', this, 'newBladeset');
	this.importBladesFromAppButton = new Button('Import Blades from App', this, 'importBladesFromApp');
	this.launchJsDocButton = new Button('Show JsDoc', this, 'showJsDocs');
	this.launchAppButton = new Button('Launch App', this, 'launchApp');
	this.exportWarButton = new Button('Export WAR', this, 'exportWar');

	oPresentationModel.appsScreen.apps.addListener(new ConditionalChangeListener(
		this._updateImportBladesFromAppButton.bind(this), this.visible, true), true);
}

Core.extend(AppDetailScreen, PresentationNode);

AppDetailScreen.prototype.displayApp = function(sApp) {
	this.m_oPresentationModel.setCurrentSection('sdk');
	this.m_oPresentationModel.getDashboardService().getApp(sApp, this._refreshAppScreen.bind(this, sApp), this.m_fOnServiceError);
};

AppDetailScreen.prototype.getBladeset = function(sBladeset) {
	var pNodes = this.bladesets.nodes('*', {
		bladesetName: sBladeset
	}).getNodesArray();

	return (pNodes.length == 1) ? pNodes[0] : null;
};

AppDetailScreen.prototype.newBladeset = function() {
	this.m_oPresentationModel.dialog.showDialog('newBladesetDialog');
};

AppDetailScreen.prototype.importBladesFromApp = function() {
	this.m_oPresentationModel.dialog.showDialog('importBladesFromAppDialog');
};

AppDetailScreen.prototype.showJsDocs = function() {
	window.location = '/dashboard-services/appjsdoc/' + this.appName.getValue() + '/jsdoc/index.html';
};

AppDetailScreen.prototype.launchApp = function() {
	var sAppUrl = this.appName.getValue();
	this.m_oPresentationModel.getWindowOpenerService().openWindow('/' + sAppUrl);
};

AppDetailScreen.prototype.exportWar = function() {
	var sWarUrl = this.m_oPresentationModel.getDashboardService().getWarUrl(this.appName.getValue());

	window.location = sWarUrl;
};

AppDetailScreen.prototype._updateImportBladesFromAppButton = function() {
	if (this.m_oPresentationModel.appsScreen.apps.getPresentationNodesArray().length <= 1) {
		this.importBladesFromAppButton.tooltipLabel.setValue('There are no other apps to import from');
		this.importBladesFromAppButton.tooltipVisible.setValue(true);
		this.importBladesFromAppButton.enabled.setValue(false);
	} else {
		this.importBladesFromAppButton.tooltipLabel.setValue('');
		this.importBladesFromAppButton.tooltipVisible.setValue(false);
		this.importBladesFromAppButton.enabled.setValue(true);
	}
};

AppDetailScreen.prototype._refreshAppScreen = function(sApp, mBladesets) {
	this.appName.setValue(sApp);

	var pBladesetPresentationModels = [];
	for (var sBladesetName in mBladesets) {
		var pBlades = mBladesets[sBladesetName];
		var oBladesetPresentationModel = new BladesetPresentationNode(sBladesetName,
			pBlades, this.m_oPresentationModel);
		pBladesetPresentationModels.push(oBladesetPresentationModel);
	}

	this.bladesets.updateList(pBladesetPresentationModels);

	this.m_oPresentationModel.hideAllScreens();
	this.visible.setValue(true);
};

AppDetailScreen.prototype._onServiceError = function(sErrorMessage) {
	this.m_oPresentationModel.dialog.displayNotification(sErrorMessage);
};

module.exports = AppDetailScreen;
