'use strict';

var Fixture = require('br/test/Fixture');
var Core = require('br/Core');
var PresentationModelFixture = require('br/presenter/testing/PresentationModelFixture');
var WindowOpenFixture = require("brjs/dashboard/app/testing/WindowOpenFixture");
var DummyLocalStorage = require("brjs/dashboard/app/service/dashboard/DummyLocalStorage");
var BrowserDetectorStub = require("brjs/dashboard/app/testing/BrowserDetectorStub");
var PageUrlProviderStub = require("brjs/dashboard/app/service/url/PageUrlProviderStub");
var XhrFactoryStub = require("brjs/dashboard/app/testing/XhrFactoryStub");
var WindowOpenerProviderStub = require("brjs/dashboard/app/service/window/WindowOpenerProviderStub");
var DashboardProvider = require("brjs/dashboard/app/service/dashboard/DashboardProvider");
var DashboardApp = require("brjs/dashboard/app/DashboardApp");

function DashboardFixture(oViewFixture, oPageUrlFixture, oUrlRequestServiceFixture, oLocalStorageFixture, oBrowserFixture) {
	this.m_oViewFixture = oViewFixture;
	this.m_oModelFixture = new PresentationModelFixture();
	this.m_oPageUrlFixture = oPageUrlFixture;
	this.m_oWindowOpenFixture = new WindowOpenFixture();
	this.m_oLocalStorageFixture = oLocalStorageFixture;
	this.m_oBrowserFixture = oBrowserFixture;
	this.m_oUrlRequestServiceFixture = oUrlRequestServiceFixture;
	this.m_eRootElement = null;
	this.oApp = null;
}

Core.inherit(DashboardFixture, Fixture);

DashboardFixture.prototype.setUp = function() {
	this.m_oLocalStorage = new DummyLocalStorage();
	this.m_oLocalStorageFixture.setLocalStorage(this.m_oLocalStorage);
	this.m_oDummyBrowserDetector = new BrowserDetectorStub();
	this.m_oBrowserFixture.setBrowserDetector(this.m_oDummyBrowserDetector);
};

DashboardFixture.prototype.addSubFixtures = function(oFixtureRegistry) {
	oFixtureRegistry.addFixture('model', this.m_oModelFixture);
	oFixtureRegistry.addFixture('view', this.m_oViewFixture);
	oFixtureRegistry.addFixture('service', this.m_oUrlRequestServiceFixture);
	oFixtureRegistry.addFixture('windowOpened', this.m_oWindowOpenFixture);
};

DashboardFixture.prototype.canHandleExactMatch = function() {
	return false;
};

DashboardFixture.prototype.canHandleProperty = function(sProperty) {
	return sProperty == 'loaded' || sProperty == 'disableLocalStorage';
};

DashboardFixture.prototype.tearDown = function() {
	if (this.oApp) {
		this.oApp.tearDown();
		this.oApp = null;
		document.body.removeChild(this.m_eRootElement);
		this.m_eRootElement = null;
	}
};

DashboardFixture.prototype.doGiven = function(sProperty, vValue) {
	if (sProperty == 'loaded') {
		this._loadApp();
	} else if (sProperty == 'disableLocalStorage') {
		this.m_oLocalStorage = {};
	} else {
		fail('given is not allowed for property ' + sProperty);
	}
};

DashboardFixture.prototype.doWhen = function(sProperty, vValue) {
	fail('when is not allowed for the dashboard fixture');
};

DashboardFixture.prototype.doThen = function(sProperty, vValue) {
	fail('then is not allowed for the dashboard fixture');
};

DashboardFixture.prototype._loadApp = function() {
	var oPageUrlService = new PageUrlProviderStub('/test/baseurl/');
	this.m_eRootElement = document.createElement('div');

	var oXhrFactory = new XhrFactoryStub();
	var oDashboardService = new DashboardProvider(oXhrFactory, '');
	var oWindowOpenerService = new WindowOpenerProviderStub();

	this.oApp = new DashboardApp(oDashboardService, oPageUrlService, oWindowOpenerService, this.m_eRootElement, this.m_oLocalStorage, this.m_oDummyBrowserDetector);

	this.m_oModelFixture.setComponent(this.oApp);
	this.m_oViewFixture.setViewElement(this.m_eRootElement);
	this.m_oPageUrlFixture.setPageUrlService(oPageUrlService);
	this.m_oWindowOpenFixture.setWindowOpenerService(oWindowOpenerService);
	this.m_oUrlRequestServiceFixture.setXhrFactory(oXhrFactory);
	document.body.appendChild(this.m_eRootElement);
};

module.exports = DashboardFixture;
