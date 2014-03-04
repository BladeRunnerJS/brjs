caplinx.dashboard.app.testing.DashboardFixtureFactory = function ()
{
};
br.Core.inherit(caplinx.dashboard.app.testing.DashboardFixtureFactory, br.test.FixtureFactory);

caplinx.dashboard.app.testing.DashboardFixtureFactory.prototype.addFixtures = function(oTestRunner)
{
	var oViewFixture = new br.test.ViewFixture();
	var oPageUrlFixture = new caplinx.dashboard.app.testing.PageUrlFixture();
	var oRequestUrlFixture = new caplinx.dashboard.app.testing.RequestUrlFixture();
	var oLocalStorageFixture =  new caplinx.dashboard.app.testing.LocalStorageFixture();
	var oBrowserDetectorFixture =  new caplinx.dashboard.app.testing.BrowserDetectorFixture;
	var oDashboardFixture =  new caplinx.dashboard.app.testing.DashboardFixture(oViewFixture, oPageUrlFixture, oRequestUrlFixture, oLocalStorageFixture, oBrowserDetectorFixture);

	oRequestUrlFixture.addCannedResponse("DEFAULT_APPS", '200 ["Example App"]');

	oTestRunner.addFixture("dash", oDashboardFixture);
	oTestRunner.addFixture("page", oPageUrlFixture);
	oTestRunner.addFixture("storage", oLocalStorageFixture);
	oTestRunner.addFixture("browser", oBrowserDetectorFixture);
};

caplinx.dashboard.app.testing.DashboardFixtureFactory.prototype.resetFixtures = function()
{

};

caplinx.dashboard.app.testing.DashboardFixtureFactory.prototype.setUp = function()
{
	document.body.focus();
};