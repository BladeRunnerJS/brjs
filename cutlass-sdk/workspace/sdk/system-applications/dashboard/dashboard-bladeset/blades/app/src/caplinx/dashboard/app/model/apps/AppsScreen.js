caplinx.dashboard.app.model.apps.AppsScreen = function(oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
	this.m_fRefreshAppsScreen = this._refreshAppsScreen.bind(this);
	this.m_fUpdateAppsScreen = this._updateAppsScreen.bind(this);
	this.m_fOnServiceError = this._onServiceError.bind(this);
	this.m_sImportingAppName = null;
	
	this.splashScreen = new caplinx.dashboard.app.model.apps.SplashScreen(oPresentationModel);
	this.visible = new br.presenter.property.WritableProperty(false);
	this.apps = new br.presenter.node.NodeList([], caplinx.dashboard.app.model.apps.AppSummaryPresentationNode);
	this.newAppButton = new caplinx.dashboard.app.model.form.Button("New App", this, "newApp");
	this.importMotifFromZipButton = new caplinx.dashboard.app.model.form.Button("Import Motif from ZIP", this, "importMotifFromZip");
	
	var oBrowserDetector = this.m_oPresentationModel.getBrowserDetector();
	if (oBrowserDetector.getBrowserName() == "ie" && oBrowserDetector.getBrowserVersion() == "9")
	{
		this.importMotifFromZipButton.tooltipLabel.setValue( "This feature is not supported in your browser." );
		this.importMotifFromZipButton.tooltipVisible.setValue( true );
		this.importMotifFromZipButton.enabled.setValue( false );
	}
};
br.extend(caplinx.dashboard.app.model.apps.AppsScreen, br.presenter.node.PresentationNode);

caplinx.dashboard.app.model.apps.AppsScreen.prototype.displayApps = function()
{
	this.m_oPresentationModel.setCurrentSection("sdk");
	this.m_oPresentationModel.getDashboardService().getApps(this.m_fRefreshAppsScreen, this.m_fOnServiceError);
};

caplinx.dashboard.app.model.apps.AppsScreen.prototype.setImportingAppName = function( sImportingAppName )
{
	this.m_sImportingAppName = sImportingAppName;
};


caplinx.dashboard.app.model.apps.AppsScreen.prototype.updateApps = function()
{
	this.m_oPresentationModel.getDashboardService().getApps(this.m_fUpdateAppsScreen, this.m_fOnServiceError);
};

caplinx.dashboard.app.model.apps.AppsScreen.prototype.newApp = function()
{
	this.m_oPresentationModel.dialog.showDialog("newAppDialog");
};

caplinx.dashboard.app.model.apps.AppsScreen.prototype.importMotifFromZip = function()
{
	this.m_oPresentationModel.dialog.showDialog("importMotifDialog");
};

caplinx.dashboard.app.model.apps.AppsScreen.prototype._refreshAppsScreen = function(pApps)
{
	this._updateAppsScreen(pApps);
	this.m_oPresentationModel.hideAllScreens();
	this.visible.setValue(true);
};

caplinx.dashboard.app.model.apps.AppsScreen.prototype._updateAppsScreen = function(pApps)
{
	var pPresentationNodes = [];

	for(var i = 0, l = pApps.length; i < l; ++i)
	{
		var sAppName = pApps[i];
		var sAppInfoUrl = "../#apps/" + sAppName;
		var sAppImageUrl = "url('" + this.m_oPresentationModel.getDashboardService().getAppImageUrl(sAppName) + "')";
		
		pPresentationNodes.push(new caplinx.dashboard.app.model.apps.AppSummaryPresentationNode(sAppName, sAppInfoUrl, sAppImageUrl));
	}
	

	if( this.m_sImportingAppName !== null )
	{
		var sLoadingImageUrl = "url('" + this.m_oPresentationModel.getDashboardService().getAppImageUrl() + "')";
		var oPlaceHolderAppNode = new caplinx.dashboard.app.model.apps.AppSummaryPresentationNode(this.m_sImportingAppName, "", sLoadingImageUrl );
		pPresentationNodes.push( oPlaceHolderAppNode );
	}

	this.apps.updateList(pPresentationNodes);
};

caplinx.dashboard.app.model.apps.AppsScreen.prototype._onServiceError = function(sErrorResponseText)
{
	this.m_oPresentationModel.dialog.displayNotification(sErrorResponseText);
};
