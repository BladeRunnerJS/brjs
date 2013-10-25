caplinx.dashboard.app.model.app.AppDetailScreen = function(oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnServiceError = this._onServiceError.bind(this);
	
	this.visible = new br.presenter.property.WritableProperty(false);
	this.appName = new br.presenter.property.WritableProperty();
	this.bladesets = new br.presenter.node.NodeList([], caplinx.dashboard.app.model.app.BladesetPresentationNode);
	
	this.newBladesetButton = new caplinx.dashboard.app.model.form.Button("New Bladeset", this, "newBladeset");
	this.importBladesFromAppButton = new caplinx.dashboard.app.model.form.Button("Import Blades from App", this, "importBladesFromApp");
	this.launchJsDocButton = new caplinx.dashboard.app.model.form.Button("Show JsDoc", this, "showJsDocs");
	this.launchAppButton = new caplinx.dashboard.app.model.form.Button("Launch App", this, "launchApp");
	this.exportWarButton = new caplinx.dashboard.app.model.form.Button("Export WAR", this, "exportWar");
	
	oPresentationModel.appsScreen.apps.addListener(new caplinx.dashboard.app.model.ConditionalChangeListener(
		this, "_updateImportBladesFromAppButton", this.visible, true), true);
};
br.extend(caplinx.dashboard.app.model.app.AppDetailScreen, br.presenter.node.PresentationNode);

caplinx.dashboard.app.model.app.AppDetailScreen.prototype.displayApp = function(sApp)
{
	this.m_oPresentationModel.setCurrentSection("sdk");
	this.m_oPresentationModel.getDashboardService().getApp(sApp, this._refreshAppScreen.bind(this, sApp), this.m_fOnServiceError);
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype.getBladeset = function(sBladeset)
{
	var pNodes = this.bladesets.nodes("*", {bladesetName:sBladeset}).getNodesArray();
	
	return (pNodes.length == 1) ? pNodes[0] : null;
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype.newBladeset = function()
{
	this.m_oPresentationModel.dialog.showDialog("newBladesetDialog");
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype.importBladesFromApp = function()
{
	this.m_oPresentationModel.dialog.showDialog("importBladesFromAppDialog");
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype.showJsDocs = function()
{
	window.location = "/dashboard-services/appjsdoc/" + this.appName.getValue() + "/jsdoc-toolkit/index.html";
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype.launchApp = function()
{
	var sAppUrl = this.appName.getValue();
	this.m_oPresentationModel.getWindowOpenerService().openWindow(
		this.m_oPresentationModel.getPageUrlService().getRootUrl() + sAppUrl);
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype.exportWar = function()
{
	var sWarUrl = this.m_oPresentationModel.getDashboardService().getWarUrl(this.appName.getValue());
	
	window.location = sWarUrl;
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype._updateImportBladesFromAppButton = function()
{
	if (this.m_oPresentationModel.appsScreen.apps.getPresentationNodesArray().length <= 1)
	{
		this.importBladesFromAppButton.tooltipLabel.setValue( "There are no other apps to import from" );
		this.importBladesFromAppButton.tooltipVisible.setValue( true );
		this.importBladesFromAppButton.enabled.setValue(false);
	}
	else
	{
		this.importBladesFromAppButton.tooltipLabel.setValue( "" );
		this.importBladesFromAppButton.tooltipVisible.setValue( false );
		this.importBladesFromAppButton.enabled.setValue(true);
	}
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype._refreshAppScreen = function(sApp, mBladesets)
{
	this.appName.setValue(sApp);
	
	var pBladesetPresentationModels = [];
	for(var sBladesetName in mBladesets)
	{
		var pBlades = mBladesets[sBladesetName];
		var oBladesetPresentationModel = new caplinx.dashboard.app.model.app.BladesetPresentationNode(sBladesetName,
			pBlades, this.m_oPresentationModel);
		pBladesetPresentationModels.push(oBladesetPresentationModel);
	}
	
	this.bladesets.updateList(pBladesetPresentationModels);
	
	this.m_oPresentationModel.hideAllScreens();
	this.visible.setValue(true);
};

caplinx.dashboard.app.model.app.AppDetailScreen.prototype._onServiceError = function(sErrorMessage)
{
	this.m_oPresentationModel.dialog.displayNotification(sErrorMessage);
};
