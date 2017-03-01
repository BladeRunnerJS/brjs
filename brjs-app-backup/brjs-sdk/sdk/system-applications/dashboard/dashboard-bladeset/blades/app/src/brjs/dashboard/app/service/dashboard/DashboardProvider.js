'use strict';

var Core = require('br/Core');
var DashboardService = require("brjs/dashboard/app/service/dashboard/DashboardService");

function DashboardProvider(oXhrFactory, requestPrefix) {
	this.m_oXhrFactory = oXhrFactory;
	this.m_sRequestPrefix = requestPrefix;
	this.m_bTestRunInProgress = false;
}

Core.inherit(DashboardProvider, DashboardService);

DashboardProvider.prototype.setIsLoadingProperty = function(oIsLoadingProperty) {
	this.m_oIsLoading = oIsLoadingProperty;
};

DashboardProvider.prototype.setLoadingTextProperty = function(oLoadingTextProperty) {
	this.m_oLoadingText = oLoadingTextProperty;
};

DashboardProvider.prototype.getApps = function(fCallback, fErrorCallback) {
	this._makeGetRequest(this.m_sRequestPrefix + '/apps', fCallback, fErrorCallback, true, 'Retrieving Apps');
};

DashboardProvider.prototype.getApp = function(sApp, fCallback, fErrorCallback) {
	this._makeGetRequest(this.m_sRequestPrefix + '/apps/' + sApp, fCallback, fErrorCallback, true, 'Retrieving App');
};

DashboardProvider.prototype.getAppImageUrl = function(sApp) {
	return this.m_sRequestPrefix + '/apps/' + sApp + '/thumb';
};

DashboardProvider.prototype.getTestResultsUrl = function() {
	return '/dashboard-services/test-results/index.html';
};

DashboardProvider.prototype.importMotif = function(sNewApp, sNamespace, oFile, fCallback, fErrorCallback) {
	if (window.FormData) {
		var oFormData = new FormData();
		oFormData.append('command', 'import-motif');
		oFormData.append('namespace', sNamespace);
		oFormData.append('file', oFile);
		this._makePostRequest(this.m_sRequestPrefix + '/apps/' + sNewApp, oFormData, fCallback, fErrorCallback, true, 'Importing Motif');
	} else {
		fErrorCallback('Browser does not support XHR2.');
	}
};

DashboardProvider.prototype.getWarUrl = function(sApp) {
	return this.m_sRequestPrefix + '/export/' + sApp;
};

DashboardProvider.prototype.importBlades = function(sSourceApp, pBladesetsMap, sTargetApp, fCallback, fErrorCallback) {
	var oData = {
		command: 'import-blades',
		app: sSourceApp,
		bladesets: pBladesetsMap
	};
	this._makePostRequest(this.m_sRequestPrefix + '/apps/' + sTargetApp, oData, fCallback, fErrorCallback, true, 'Importing Blade');
};

DashboardProvider.prototype.createApp = function(sApp, sNamespace, fCallback, fErrorCallback) {
	var oData = {
		command: 'create-app',
		namespace: sNamespace
	};
	this._makePostRequest(this.m_sRequestPrefix + '/apps/' + sApp, oData, fCallback, fErrorCallback, true, 'Creating App');
};

DashboardProvider.prototype.createBladeset = function(sApp, sBladeset, fCallback, fErrorCallback) {
	var oData = {
		command: 'create-bladeset'
	};
	this._makePostRequest(this.m_sRequestPrefix + '/apps/' + sApp + '/' + sBladeset, oData, fCallback, fErrorCallback, true, 'Creating Bladeset');
};

DashboardProvider.prototype.createBlade = function(sApp, sBladeset, sBlade, fCallback, fErrorCallback) {
	var oData = {
		command: 'create-blade'
	};
	this._makePostRequest(this.m_sRequestPrefix + '/apps/' + sApp + '/' + sBladeset + '/' + sBlade, oData, fCallback, fErrorCallback, true, 'Creating Blade');
};

DashboardProvider.prototype.runBladesetTests = function(sApp, sBladeset, fProgressListener, fErrorCallback) {
	// TODO: does this need to specify test type and whether to recurse?
	var oData = {
		command: 'test',
		type: 'ALL',
		recurse: 'false'
	};
	this._makePostRequest(this.m_sRequestPrefix + '/test/' + sApp + '/' + sBladeset, oData, fProgressListener, fErrorCallback, false, 'Running Bladeset Tests');
};

DashboardProvider.prototype.runBladeTests = function(sApp, sBladeset, sBlade, fProgressListener, fErrorCallback) {
	// TODO: does this need to specify test type?
	var oData = {
		command: 'test',
		type: 'ALL'
	};
	this._makePostRequest(this.m_sRequestPrefix + '/test/' + sApp + '/' + sBladeset + '/' + sBlade, oData, fProgressListener, fErrorCallback, false, 'Running Blade Tests');
};

DashboardProvider.prototype.getCurrentReleaseNote = function(fCallback, fErrorCallback) {
	this._makeGetRequest(this.m_sRequestPrefix + '/note/latest', fCallback, fErrorCallback, false, 'Retrieving Latest Release Notes');
};

DashboardProvider.prototype.getSdkVersion = function(fCallback, fErrorCallback) {
	var fGetSdkVersionCallback = function(oResponse) {
		if (oResponse.Version) {
				fCallback(oResponse.Version);
				} else {
				fErrorCallback('Could not determine SDK version.');
				}
		};
		this._makeGetRequest(this.m_sRequestPrefix + '/sdk/version', fGetSdkVersionCallback, fErrorCallback, true, '');
	};

	DashboardProvider.prototype.getTestRunInProgress = function() {
		return this.m_bTestRunInProgress;
	};

	DashboardProvider.prototype.setTestRunInProgress = function(bValue) {
		this.m_bTestRunInProgress = bValue;
	};

	/* internal methods */

	DashboardProvider.prototype._makeGetRequest = function(url, fCallback, fErrorCallback, bIsJSONResponse, sLoadingMessage) {
		this._makeRequest('GET', url, null, fCallback, fErrorCallback, bIsJSONResponse, sLoadingMessage);
	};

	DashboardProvider.prototype._makePostRequest = function(url, oData, fCallback, fErrorCallback, bIsJSONResponse, sLoadingMessage) {
		this._makeRequest('POST', url, oData, fCallback, fErrorCallback, bIsJSONResponse, sLoadingMessage);
	};

	DashboardProvider.prototype._makeRequest = function(method, url, oData, fCallback, fErrorCallback, bIsJSONResponse, sLoadingMessage) {
		var oXhr = this.m_oXhrFactory.getRequestObject();

		var oThis = this;

		oThis._setLoading(true, sLoadingMessage);

		oXhr.onreadystatechange = function() {
			if (oXhr.readyState == 4) {
				oThis._setLoading(false, '');

				if (oXhr.status == 200) {
					if (oXhr.responseText.trim() === '' || !bIsJSONResponse) {
						fCallback(oXhr.responseText.trim());
					} else {
						fCallback(JSON.parse(oXhr.responseText));
					}
				} else {
					var oResponseObject = JSON.parse(oXhr.responseText);

					fErrorCallback((oResponseObject.message !== null) ? oResponseObject.message : '');
				}
				}
		};

		oXhr.open(method, url, true);
		if (oData === null) {
			oXhr.send('');
			} else {
			if (window.FormData && oData instanceof FormData) {
				oXhr.send(oData);
				} else {
				oXhr.send(JSON.stringify(oData));
				}
			}
	};

	DashboardProvider.prototype._setLoading = function(bIsLoading, sLoadingMessage) {
		if (this.m_oIsLoading && this.m_oLoadingText) {
			this.m_oIsLoading.setValue(bIsLoading);

			if (sLoadingMessage !== null) {
				this.m_oLoadingText.setValue(sLoadingMessage);
				}
			}
	};


	module.exports = DashboardProvider;
