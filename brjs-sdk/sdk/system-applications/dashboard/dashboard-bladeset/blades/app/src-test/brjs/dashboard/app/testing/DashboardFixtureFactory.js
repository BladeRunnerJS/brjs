brjs.dashboard.app.testing.DashboardFixtureFactory = function ()
{
};
br.Core.inherit(brjs.dashboard.app.testing.DashboardFixtureFactory, br.test.FixtureFactory);

brjs.dashboard.app.testing.DashboardFixtureFactory.prototype.addFixtures = function(oTestRunner)
{
	var oViewFixture = new br.test.ViewFixture();
	var oPageUrlFixture = new brjs.dashboard.app.testing.PageUrlFixture();
	var oRequestUrlFixture = new brjs.dashboard.app.testing.RequestUrlFixture();
	var oLocalStorageFixture =  new brjs.dashboard.app.testing.LocalStorageFixture();
	var oBrowserDetectorFixture =  new brjs.dashboard.app.testing.BrowserDetectorFixture;
	var oDashboardFixture =  new brjs.dashboard.app.testing.DashboardFixture(oViewFixture, oPageUrlFixture, oRequestUrlFixture, oLocalStorageFixture, oBrowserDetectorFixture);

	oRequestUrlFixture.addCannedResponse("DEFAULT_APPS", '200 ["Example App"]');

	oTestRunner.addFixture("dash", oDashboardFixture);
	oTestRunner.addFixture("page", oPageUrlFixture);
	oTestRunner.addFixture("storage", oLocalStorageFixture);
	oTestRunner.addFixture("browser", oBrowserDetectorFixture);
};

brjs.dashboard.app.testing.DashboardFixtureFactory.prototype.resetFixtures = function()
{

};

brjs.dashboard.app.testing.DashboardFixtureFactory.prototype.setUp = function()
{
	document.body.focus();
};