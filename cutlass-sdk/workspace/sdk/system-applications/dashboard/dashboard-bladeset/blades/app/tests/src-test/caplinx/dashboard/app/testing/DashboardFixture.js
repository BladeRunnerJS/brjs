caplinx.dashboard.app.testing.DashboardFixture = function(oViewFixture, oPageUrlFixture, oUrlRequestServiceFixture, oLocalStorageFixture, oBrowserFixture) 
{
	this.m_oViewFixture = oViewFixture;
	this.m_oModelFixture = new br.presenter.testing.PresentationModelFixture();
	this.m_oPageUrlFixture = oPageUrlFixture;
	this.m_oWindowOpenFixture = new caplinx.dashboard.app.testing.WindowOpenFixture();
	this.m_oLocalStorageFixture = oLocalStorageFixture;	
	this.m_oBrowserFixture = oBrowserFixture;
	this.m_oUrlRequestServiceFixture = oUrlRequestServiceFixture;
	this.m_eRootElement = null;
	this.oApp = null;
};

br.inherit(caplinx.dashboard.app.testing.DashboardFixture, br.test.Fixture);

caplinx.dashboard.app.testing.DashboardFixture.prototype.setUp = function() 
{
	this.m_oLocalStorage = new caplinx.dashboard.app.service.dashboard.DummyLocalStorage();
	this.m_oLocalStorageFixture.setLocalStorage(this.m_oLocalStorage);
	this.m_oDummyBrowserDetector = new caplinx.dashboard.app.testing.BrowserDetectorStub();
	this.m_oBrowserFixture.setBrowserDetector(this.m_oDummyBrowserDetector);
};

caplinx.dashboard.app.testing.DashboardFixture.prototype.addSubFixtures = function(oFixtureRegistry) 
{
	oFixtureRegistry.addFixture("model", this.m_oModelFixture);
	oFixtureRegistry.addFixture("view", this.m_oViewFixture);
	oFixtureRegistry.addFixture("service", this.m_oUrlRequestServiceFixture);
	oFixtureRegistry.addFixture("windowOpened", this.m_oWindowOpenFixture);
};

caplinx.dashboard.app.testing.DashboardFixture.prototype.canHandleExactMatch = function() 
{
	return false
};

caplinx.dashboard.app.testing.DashboardFixture.prototype.canHandleProperty = function(sProperty) 
{
	return sProperty == "loaded" || sProperty == "disableLocalStorage";
};

caplinx.dashboard.app.testing.DashboardFixture.prototype.tearDown = function() 
{
	if(this.oApp)
	{
		this.oApp.tearDown();
		this.oApp = null;
		document.body.removeChild(this.m_eRootElement);
		this.m_eRootElement = null;
	}
};

caplinx.dashboard.app.testing.DashboardFixture.prototype.doGiven = function(sProperty, vValue) 
{
	if (sProperty == "loaded")
	{
		this._loadApp();
	}
	else if (sProperty == "disableLocalStorage")
	{
		this.m_oLocalStorage = {};
	}
	else
	{
		fail("given is not allowed for property " + sProperty);
	}
};

caplinx.dashboard.app.testing.DashboardFixture.prototype.doWhen = function(sProperty, vValue) 
{
	fail("when is not allowed for the dashboard fixture")
};

caplinx.dashboard.app.testing.DashboardFixture.prototype.doThen = function(sProperty, vValue) 
{
	fail("then is not allowed for the dashboard fixture")
};

caplinx.dashboard.app.testing.DashboardFixture.prototype._loadApp = function()
{
	var oPageUrlService = new caplinx.dashboard.app.service.url.PageUrlProviderStub("/test/baseurl/");
	this.m_eRootElement = document.createElement("div");

	var oXhrFactory = new caplinx.dashboard.app.testing.XhrFactoryStub();
	var oDashboardService = new caplinx.dashboard.app.service.dashboard.DashboardProvider(oXhrFactory, "");
	var oWindowOpenerService = new caplinx.dashboard.app.service.window.WindowOpenerProviderStub();

	this.oApp = new caplinx.dashboard.app.DashboardApp(oDashboardService, oPageUrlService, oWindowOpenerService, this.m_eRootElement, this.m_oLocalStorage, this.m_oDummyBrowserDetector);

	this.m_oModelFixture.setComponent(this.oApp);
	this.m_oViewFixture.setViewElement(this.m_eRootElement);
	this.m_oPageUrlFixture.setPageUrlService(oPageUrlService);
	this.m_oWindowOpenFixture.setWindowOpenerService(oWindowOpenerService);
	this.m_oUrlRequestServiceFixture.setXhrFactory(oXhrFactory);
	document.body.appendChild(this.m_eRootElement);
}
