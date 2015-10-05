'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var NodeList = require('br/presenter/node/NodeList');
var WritableProperty = require('br/presenter/property/WritableProperty');
var SplashScreen = require("brjs/dashboard/app/model/apps/SplashScreen");
var AppSummaryPresentationNode = require("brjs/dashboard/app/model/apps/AppSummaryPresentationNode");
var Button = require("brjs/dashboard/app/model/form/Button");

function AppsScreen(oPresentationModel) {
	this.m_oPresentationModel = oPresentationModel;
	this.m_fRefreshAppsScreen = this._refreshAppsScreen.bind(this);
	this.m_fUpdateAppsScreen = this._updateAppsScreen.bind(this);
	this.m_fOnServiceError = this._onServiceError.bind(this);
	this.m_sImportingAppName = null;

	this.splashScreen = new SplashScreen(oPresentationModel);
	this.visible = new WritableProperty(false);
	this.apps = new NodeList([], AppSummaryPresentationNode);
	this.newAppButton = new Button('New App', this, 'newApp');
	this.importMotifFromZipButton = new Button('Import Motif from ZIP', this, 'importMotifFromZip');

	var oBrowserDetector = this.m_oPresentationModel.getBrowserDetector();
	if (oBrowserDetector.getBrowserName() == 'ie' && oBrowserDetector.getBrowserVersion() == '9') {
		this.importMotifFromZipButton.tooltipLabel.setValue('This feature is not supported in your browser.');
		this.importMotifFromZipButton.tooltipVisible.setValue(true);
		this.importMotifFromZipButton.enabled.setValue(false);
	}
}

Core.extend(AppsScreen, PresentationNode);

AppsScreen.prototype.displayApps = function() {
	this.m_oPresentationModel.setCurrentSection('sdk');
	this.m_oPresentationModel.getDashboardService().getApps(this.m_fRefreshAppsScreen, this.m_fOnServiceError);
};

AppsScreen.prototype.setImportingAppName = function(sImportingAppName) {
	this.m_sImportingAppName = sImportingAppName;
};


AppsScreen.prototype.updateApps = function() {
	this.m_oPresentationModel.getDashboardService().getApps(this.m_fUpdateAppsScreen, this.m_fOnServiceError);
};

AppsScreen.prototype.newApp = function() {
	this.m_oPresentationModel.dialog.showDialog('newAppDialog');
};

AppsScreen.prototype.importMotifFromZip = function() {
	this.m_oPresentationModel.dialog.showDialog('importMotifDialog');
};

AppsScreen.prototype._refreshAppsScreen = function(pApps) {
	this._updateAppsScreen(pApps);
	this.m_oPresentationModel.hideAllScreens();
	this.visible.setValue(true);
};

AppsScreen.prototype._updateAppsScreen = function(pApps) {
	var pPresentationNodes = [];

	for (var i = 0, l = pApps.length; i < l; ++i) {
		var sAppName = pApps[i];
		var sAppInfoUrl = '#apps/' + sAppName;
		var sAppImageUrl = "url('" + this.m_oPresentationModel.getDashboardService().getAppImageUrl(sAppName) + "')";

		pPresentationNodes.push(new AppSummaryPresentationNode(sAppName, sAppInfoUrl, sAppImageUrl));
	}

	if (this.m_sImportingAppName !== null) {
		var sLoadingImageUrl = "url('" + this.m_oPresentationModel.getDashboardService().getAppImageUrl() + "')";
		var oPlaceHolderAppNode = new AppSummaryPresentationNode(this.m_sImportingAppName, '', sLoadingImageUrl);
		pPresentationNodes.push(oPlaceHolderAppNode);
	}

	this.apps.updateList(pPresentationNodes);
};

AppsScreen.prototype._onServiceError = function(sErrorResponseText) {
	this.m_oPresentationModel.dialog.displayNotification(sErrorResponseText);
};

module.exports = AppsScreen;
