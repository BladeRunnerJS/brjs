'use strict';

var SimpleFrame = require('br/component/SimpleFrame');
var PresenterComponent = require('br/presenter/component/PresenterComponent');
var InvalidBrowserDecider = require("brjs/dashboard/app/service/browserdetector/InvalidBrowserDecider");
var DashboardPresentationModel = require("brjs/dashboard/app/model/DashboardPresentationModel");

function DashboardApp(oDashboardService, oPageUrlService, oWindowOpenerService, eDisplayElement, oLocalStorage, oBrowserDetector) {
	this.m_oDashboardService = oDashboardService;
	this.m_oPageUrlService = oPageUrlService;
	this.m_oWindowOpenerService = oWindowOpenerService;
	this.m_eDisplayElement = eDisplayElement;
	this.m_oBrowserDetector = oBrowserDetector;
	this.m_oInvalidBrowserDecider = new InvalidBrowserDecider(oBrowserDetector);

	this.m_oPresentationModel = new DashboardPresentationModel(oDashboardService, oPageUrlService, oWindowOpenerService, oLocalStorage, oBrowserDetector);
	this.m_oPresenterComponent = new PresenterComponent('brjs.dashboard.app.root', this.m_oPresentationModel);

	var frame = new SimpleFrame(this.m_oPresenterComponent, null, null);
	eDisplayElement.appendChild(frame.getElement());
	frame.trigger('attach');

	this.m_bAppsLoaded = false;
	oPageUrlService.addPageUrlListener(this._onPageUrlUpdated.bind(this), true);

	this._showBrowserWarningDialogIfNeeded();
}

/**
 * @static
 */
DashboardApp.initializeLibrary = function() {};

DashboardApp.prototype.getPresentationModel = function() {
	return this.m_oPresentationModel;
};

DashboardApp.prototype.tearDown = function() {
	this.m_oPresenterComponent.onClose();
};

/**
 * @private
 */
DashboardApp.prototype._showBrowserWarningDialogIfNeeded = function() {
	if (!this.m_oInvalidBrowserDecider.isValidBrowser()) {
		this.m_oPresentationModel.dialog.browserWarningDialog.setMinimumBrowserVersions(this.m_oInvalidBrowserDecider.getMinimumBrowserVersions());
		this.m_oPresentationModel.dialog.showDialog('browserWarningDialog');
	}
};

DashboardApp.prototype._onPageUrlUpdated = function(sPageUrl) {
	this.m_oPresentationModel.dialog.visible.setValue(false);

	if (sPageUrl.match(/^#anchor-/)) {
	// Do nothing because this is a genuine internal anchor
	} else if (sPageUrl.match(/^#apps\/.*/)) {
		if (!this.m_bAppsLoaded) {
			this.m_bAppsLoaded = true;
			this.m_oPresentationModel.appsScreen.updateApps();
		}

		var sApp = sPageUrl.split('/')[1];
		this.m_oPresentationModel.appDetailScreen.displayApp(sApp);
	} else if (sPageUrl == '#note/latest') {
		this.m_oPresentationModel.releaseNoteScreen.displayReleaseNote();
	} else {
		this.m_bAppsLoaded = true;
		this.m_oPresentationModel.appsScreen.displayApps();
	}
};

module.exports = DashboardApp;
