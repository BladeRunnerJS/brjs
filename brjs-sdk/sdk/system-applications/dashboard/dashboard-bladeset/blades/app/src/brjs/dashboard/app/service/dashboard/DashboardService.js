'use strict';

var Utility = require('br/util/Utility');

function DashboardService() {
}

DashboardService.prototype.getWarUrl = function(sApp) {
	Utility.interfaceMethod('DashboardService', 'getWarUrl');
};

DashboardService.prototype.getAppImageUrl = function(sApp) {
	Utility.interfaceMethod('DashboardService', 'getAppImageUrl');
};

DashboardService.prototype.getTestResultsUrl = function() {
	Utility.interfaceMethod('DashboardService', 'getTestResultsUrl');
};

DashboardService.prototype.getApps = function(fCallback, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'getApps');
};

DashboardService.prototype.getApp = function(sApp, fCallback, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'getApp');
};

DashboardService.prototype.importMotif = function(sNewApp, sNamespace, sFileInputElemId, fCallback, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'importMotif');
};

DashboardService.prototype.importBlades = function(sSourceApp, mBlades, sTargetApp, fCallback, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'importBlades');
};

DashboardService.prototype.createApp = function(sApp, sNamespace, fCallback, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'createApp');
};

DashboardService.prototype.createBladeset = function(sApp, sBladeset, fCallback, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'createBladeset');
};

DashboardService.prototype.createBlade = function(sApp, sBladeset, sBlade, fCallback, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'createBlade');
};

DashboardService.prototype.runBladesetTests = function(sApp, sBladeset, fProgressListener, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'runBladesetTests');
};

DashboardService.prototype.runBladeTests = function(sApp, sBladeset, sBlade, fProgressListener, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'runBladeTests');
};

DashboardService.prototype.getCurrentReleaseNote = function(fCallback, fErrorCallback) {
	Utility.interfaceMethod('DashboardService', 'getCurrentReleaseNote');
};

DashboardService.prototype.getTestRunInProgress = function() {
	Utility.interfaceMethod('DashboardService', 'getTestRunInProgress');
};

DashboardService.prototype.setTestRunInProgress = function(bValue) {
	Utility.interfaceMethod('DashboardService', 'setTestRunInProgress');
};

module.exports = DashboardService;
