caplinx.dashboard.app.DashboardApp = function(oDashboardService, oPageUrlService, oWindowOpenerService, eDisplayElement, oLocalStorage, oBrowserDetector)
{
	this.m_oDashboardService = oDashboardService;
	this.m_oPageUrlService = oPageUrlService;
	this.m_oWindowOpenerService = oWindowOpenerService;
	this.m_eDisplayElement = eDisplayElement;
	this.m_oBrowserDetector = oBrowserDetector;
	this.m_oInvalidBrowserDecider = new caplinx.dashboard.app.service.browserdetector.InvalidBrowserDecider(oBrowserDetector);
	
	this.m_oPresentationModel = new caplinx.dashboard.app.model.DashboardPresentationModel(oDashboardService, oPageUrlService, oWindowOpenerService, oLocalStorage, oBrowserDetector);
	this.m_oPresenterComponent = new br.presenter.component.PresenterComponent("caplinx.dashboard.app.root", this.m_oPresentationModel);
	
	// TODO: update dashboard to use new component interface.
	// TODO: add proper frame support
	eDisplayElement.appendChild(this.m_oPresenterComponent.getElement());

	//	this.m_oPresenterComponent.onOpen();
	
	this.m_bAppsLoaded = false;
	oPageUrlService.addPageUrlListener(this._onPageUrlUpdated.bind(this), true);
	
	this._showBrowserWarningDialogIfNeeded();
};

/**
 * @static
 */
caplinx.dashboard.app.DashboardApp.initializeLibrary = function()
{	
};

caplinx.dashboard.app.DashboardApp.prototype.getPresentationModel = function()
{
	return this.m_oPresentationModel;
};

caplinx.dashboard.app.DashboardApp.prototype.tearDown = function()
{
	this.m_oPresenterComponent.onClose();
};


/**
 * @private
 */
caplinx.dashboard.app.DashboardApp.prototype._showBrowserWarningDialogIfNeeded = function()
{
	if (!this.m_oInvalidBrowserDecider.isValidBrowser())
	{		
		this.m_oPresentationModel.dialog.browserWarningDialog.setMinimumBrowserVersions(this.m_oInvalidBrowserDecider.getMinimumBrowserVersions());
		this.m_oPresentationModel.dialog.showDialog("browserWarningDialog");
	}
}

caplinx.dashboard.app.DashboardApp.prototype._onPageUrlUpdated = function(sPageUrl)
{
	this.m_oPresentationModel.dialog.visible.setValue(false);
	
	if(sPageUrl.match(/^#anchor-/))
	{
		// Do nothing because this is a genuine internal anchor
	}
	else if(sPageUrl.match(/^#apps\/.*workbench$/))
	{
		var pParts = sPageUrl.match(/^#apps\/(.*)workbench$/)[1].split("/");
		this.m_oPresentationModel.workbenchScreen.displayWorkbench(pParts[0], pParts[1], pParts[2]);
	}
	else if(sPageUrl.match(/^#apps\/.*/))
	{
		if(!this.m_bAppsLoaded)
		{
			this.m_bAppsLoaded = true;
			this.m_oPresentationModel.appsScreen.updateApps();
		}
		
		var sApp = sPageUrl.split("/")[1];
		this.m_oPresentationModel.appDetailScreen.displayApp(sApp);
		
	}
	else if(sPageUrl == "#note/latest")
	{
		this.m_oPresentationModel.releaseNoteScreen.displayReleaseNote();
	}
	else
	{
		this.m_bAppsLoaded = true;
		this.m_oPresentationModel.appsScreen.displayApps();
	}
};
