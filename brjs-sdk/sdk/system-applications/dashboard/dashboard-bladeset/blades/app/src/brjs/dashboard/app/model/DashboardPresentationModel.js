brjs.dashboard.app.model.DashboardPresentationModel = function(oDashboardService, oPageUrlService, oWindowOpenerService, oLocalStorage, oBrowserDetector)
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

	this.crumbtrail = new brjs.dashboard.app.model.crumbtrail.CrumbTrail(this);

	this.appsScreen = new brjs.dashboard.app.model.apps.AppsScreen(this);
	this.appDetailScreen = new brjs.dashboard.app.model.app.AppDetailScreen(this);
	this.releaseNoteScreen = new brjs.dashboard.app.model.release.ReleaseNoteScreen(this);

	this.sdkVersion = new br.presenter.property.WritableProperty();
	this.setSdkVersion();

	this.dialog = new brjs.dashboard.app.model.dialog.Dialog(this);
	this.dialog.initialize();
};
br.Core.extend(brjs.dashboard.app.model.DashboardPresentationModel, br.presenter.PresentationModel);

brjs.dashboard.app.model.DashboardPresentationModel.prototype.getBrowserDetector = function()
{
	return this.m_oBrowserDetector;
};

brjs.dashboard.app.model.DashboardPresentationModel.prototype.getDashboardService = function()
{
	return this.m_oDashboardService;
};

brjs.dashboard.app.model.DashboardPresentationModel.prototype.getPageUrlService = function()
{
	return this.m_oPageUrlService;
};

brjs.dashboard.app.model.DashboardPresentationModel.prototype.getWindowOpenerService = function()
{
	return this.m_oWindowOpenerService;
};

brjs.dashboard.app.model.DashboardPresentationModel.prototype.getLocalStorage = function()
{
	return this.m_oLocalStorage;
};

brjs.dashboard.app.model.DashboardPresentationModel.prototype.setCurrentSection = function(sSection)
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

brjs.dashboard.app.model.DashboardPresentationModel.prototype.hideAllScreens = function()
{
	this.appsScreen.visible.setValue(false);
	this.appDetailScreen.visible.setValue(false);
	this.releaseNoteScreen.visible.setValue(false);
};

brjs.dashboard.app.model.DashboardPresentationModel.prototype.setSdkVersion = function()
{
	fSuccessCallback = function(sResponse) {
		this.sdkVersion.setValue(sResponse);
	};
	fErrorCallback = function(sResponse) {
		this.sdkVersion.setValue("Unknown");
	};
	this.m_oDashboardService.getSdkVersion(fSuccessCallback.bind(this), fErrorCallback.bind(this));
};
