'use strict';

var PresentationModel = require('br/presenter/PresentationModel');
var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');
var CrumbTrail = require("brjs/dashboard/app/model/crumbtrail/CrumbTrail");

var AppsScreen = require("brjs/dashboard/app/model/apps/AppsScreen");
var AppDetailScreen = require("brjs/dashboard/app/model/app/AppDetailScreen");
var ReleaseNoteScreen = require("brjs/dashboard/app/model/release/ReleaseNoteScreen");
var Dialog = require("brjs/dashboard/app/model/dialog/Dialog");

function DashboardPresentationModel(oDashboardService, oPageUrlService, oWindowOpenerService, oLocalStorage, oBrowserDetector) {
	this.m_oDashboardService = oDashboardService;
	this.m_oPageUrlService = oPageUrlService;
	this.m_oWindowOpenerService = oWindowOpenerService;
	this.m_oLocalStorage = oLocalStorage;
	this.m_oBrowserDetector = oBrowserDetector;

	this.currentSection = new WritableProperty('');
	this.isLoading = new WritableProperty();
	this.m_oDashboardService.setIsLoadingProperty(this.isLoading);
	this.loadingText = new WritableProperty();
	this.m_oDashboardService.setLoadingTextProperty(this.loadingText);

	this.isSDKSectionSelected = new WritableProperty();
	this.isSupportSectionSelected = new WritableProperty();

	this.crumbtrail = new CrumbTrail(this);

	this.appsScreen = new AppsScreen(this);
	this.appDetailScreen = new AppDetailScreen(this);
	this.releaseNoteScreen = new ReleaseNoteScreen(this);

	this.sdkVersion = new WritableProperty();
	this.setSdkVersion();

	this.dialog = new Dialog(this);
	this.dialog.initialize();
}

Core.extend(DashboardPresentationModel, PresentationModel);

DashboardPresentationModel.prototype.getBrowserDetector = function() {
	return this.m_oBrowserDetector;
};

DashboardPresentationModel.prototype.getDashboardService = function() {
	return this.m_oDashboardService;
};

DashboardPresentationModel.prototype.getPageUrlService = function() {
	return this.m_oPageUrlService;
};

DashboardPresentationModel.prototype.getWindowOpenerService = function() {
	return this.m_oWindowOpenerService;
};

DashboardPresentationModel.prototype.getLocalStorage = function() {
	return this.m_oLocalStorage;
};

DashboardPresentationModel.prototype.setCurrentSection = function(sSection) {
	this.currentSection.setValue(sSection);
	this.isSDKSectionSelected.setValue(false);
	this.isSupportSectionSelected.setValue(false);

	switch (sSection) {
		case 'sdk':
			this.isSDKSectionSelected.setValue(true);
			break;

		case 'support':
			this.isSupportSectionSelected.setValue(true);
			break;
	}
};

DashboardPresentationModel.prototype.hideAllScreens = function() {
	this.appsScreen.visible.setValue(false);
	this.appDetailScreen.visible.setValue(false);
	this.releaseNoteScreen.visible.setValue(false);
};

DashboardPresentationModel.prototype.setSdkVersion = function() {
	var fSuccessCallback = function(sResponse) {
		this.sdkVersion.setValue(sResponse);
	};
	var fErrorCallback = function(sResponse) {
		this.sdkVersion.setValue('Unknown');
	};
	this.m_oDashboardService.getSdkVersion(fSuccessCallback.bind(this), fErrorCallback.bind(this));
};

module.exports = DashboardPresentationModel;
