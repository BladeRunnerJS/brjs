caplinx.dashboard.app.model.DashboardPresentationModel = function(oDashboardService, oPageUrlService, oWindowOpenerService, oLocalStorage, oBrowserDetector)
{
	this.m_oDashboardService = oDashboardService;
	this.m_oPageUrlService = oPageUrlService;
	this.m_oWindowOpenerService = oWindowOpenerService;
	this.m_oLocalStorage = oLocalStorage;
	this.m_oBrowserDetector = oBrowserDetector;
	
	this.currentSection = new br.presenter.property.WritableProperty( "" );
	this.isLoading = new br.presenter.property.WritableProperty();
	this.m_oDashboardService.setIsLoadingProperty(this.isLoading);
	this.loadingText = new br.presenter.property.WritableProperty();
	this.m_oDashboardService.setLoadingTextProperty(this.loadingText);
	
	this.isSDKSectionSelected = new br.presenter.property.WritableProperty();
	this.isSupportSectionSelected = new br.presenter.property.WritableProperty();
	
	this.crumbtrail = new caplinx.dashboard.app.model.crumbtrail.CrumbTrail(this);
	
	this.appsScreen = new caplinx.dashboard.app.model.apps.AppsScreen(this);
	this.appDetailScreen = new caplinx.dashboard.app.model.app.AppDetailScreen(this);
	this.workbenchScreen = new caplinx.dashboard.app.model.workbench.WorkbenchScreen(this);
	this.releaseNoteScreen = new caplinx.dashboard.app.model.release.ReleaseNoteScreen(this);
	
	this.sdkVersion = new br.presenter.property.WritableProperty();
	this.setSdkVersion();
	
	this.dialog = new caplinx.dashboard.app.model.dialog.Dialog(this);
	this.dialog.initialize();
};
br.Core.extend(caplinx.dashboard.app.model.DashboardPresentationModel, br.presenter.PresentationModel);

caplinx.dashboard.app.model.DashboardPresentationModel.prototype.getBrowserDetector = function()
{
	return this.m_oBrowserDetector;
};

caplinx.dashboard.app.model.DashboardPresentationModel.prototype.getDashboardService = function()
{
	return this.m_oDashboardService;
};

caplinx.dashboard.app.model.DashboardPresentationModel.prototype.getPageUrlService = function()
{
	return this.m_oPageUrlService;
};

caplinx.dashboard.app.model.DashboardPresentationModel.prototype.getWindowOpenerService = function()
{
	return this.m_oWindowOpenerService;
};

caplinx.dashboard.app.model.DashboardPresentationModel.prototype.getLocalStorage = function()
{
	return this.m_oLocalStorage;
};

caplinx.dashboard.app.model.DashboardPresentationModel.prototype.setCurrentSection = function(sSection)
{
	this.currentSection.setValue( sSection );
	this.isSDKSectionSelected.setValue(false);
	this.isSupportSectionSelected.setValue(false);
	
	switch(sSection)
	{
		case "sdk":
			this.isSDKSectionSelected.setValue(true);
			break;

		case "support":
			this.isSupportSectionSelected.setValue(true);
			break;
	}
};

caplinx.dashboard.app.model.DashboardPresentationModel.prototype.hideAllScreens = function()
{
	this.appsScreen.visible.setValue(false);
	this.appDetailScreen.visible.setValue(false);
	this.workbenchScreen.visible.setValue(false);
	this.releaseNoteScreen.visible.setValue(false);
};

caplinx.dashboard.app.model.DashboardPresentationModel.prototype.setSdkVersion = function()
{
	fSuccessCallback = function(sResponse) {
		this.sdkVersion.setValue(sResponse);
	};
	fErrorCallback = function(sResponse) {
		this.sdkVersion.setValue("Unknown");
	};
	this.m_oDashboardService.getSdkVersion(fSuccessCallback.bind(this), fErrorCallback.bind(this));
};
