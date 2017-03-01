'use strict';

var ViewFixture = require('br/test/ViewFixture');
var FixtureFactory = require('br/test/FixtureFactory');
var Core = require('br/Core');

var PageUrlFixture = require("brjs/dashboard/app/testing/PageUrlFixture");
var RequestUrlFixture = require("brjs/dashboard/app/testing/RequestUrlFixture");
var LocalStorageFixture = require("brjs/dashboard/app/testing/LocalStorageFixture");
var BrowserDetectorFixture = require("brjs/dashboard/app/testing/BrowserDetectorFixture");
var DashboardFixture = require("brjs/dashboard/app/testing/DashboardFixture");

function DashboardFixtureFactory() {
}

Core.inherit(DashboardFixtureFactory, FixtureFactory);

DashboardFixtureFactory.prototype.addFixtures = function(oTestRunner) {
	var oViewFixture = new ViewFixture();
	var oPageUrlFixture = new PageUrlFixture();
	var oRequestUrlFixture = new RequestUrlFixture();
	var oLocalStorageFixture = new LocalStorageFixture();
	var oBrowserDetectorFixture = new BrowserDetectorFixture;
	var oDashboardFixture = new DashboardFixture(oViewFixture, oPageUrlFixture, oRequestUrlFixture, oLocalStorageFixture, oBrowserDetectorFixture);

	oRequestUrlFixture.addCannedResponse('DEFAULT_APPS', '200 ["Example App"]');

	oTestRunner.addFixture('dash', oDashboardFixture);
	oTestRunner.addFixture('page', oPageUrlFixture);
	oTestRunner.addFixture('storage', oLocalStorageFixture);
	oTestRunner.addFixture('browser', oBrowserDetectorFixture);
};

DashboardFixtureFactory.prototype.resetFixtures = function() {};

DashboardFixtureFactory.prototype.setUp = function() {
	document.body.focus();
};

module.exports = DashboardFixtureFactory;
